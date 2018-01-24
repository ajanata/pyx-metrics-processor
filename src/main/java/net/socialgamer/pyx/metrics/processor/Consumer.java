/**
 * Copyright (c) 2017, Andy Janata
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions
 *   and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of
 *   conditions and the following disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.socialgamer.pyx.metrics.processor;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import net.socialgamer.pyx.metrics.data.Event;
import net.socialgamer.pyx.metrics.processor.destination.Destination;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;


public class Consumer implements Runnable {

  private static final int CONSECUTIVE_NO_DATA_LOG_COUNT = 60;

  private static final Logger LOG = Logger.getLogger(Consumer.class);

  private final Provider<KafkaConsumer<String, String>> consumerProvider;
  private final Destination destination;
  private final ObjectMapper mapper;
  private final String topic;
  private KafkaConsumer<String, String> consumer = null;
  private final AtomicBoolean shutdown = new AtomicBoolean(false);

  @Inject
  public Consumer(final Provider<KafkaConsumer<String, String>> consumerProvider,
      final Destination destination, final ObjectMapper mapper,
      @Named("kafka.topic") final String topic) {
    this.consumerProvider = consumerProvider;
    this.destination = destination;
    this.mapper = mapper;
    this.topic = topic;
    LOG.info(String.format("Will consume from topic %s", topic));
  }

  private synchronized void ensureConsumer() {
    if (null == consumer) {
      LOG.info("Creating new consumer.");
      consumer = consumerProvider.get();
      final KafkaConsumer<String, String> consumerForHook = consumer;
      if (null != consumerForHook) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          LOG.info("Closing consumer.");
          shutdown.set(true);
          try {
            consumerForHook.wakeup();
            consumerForHook.close(3, TimeUnit.SECONDS);
          } catch (final Exception e) {
            // ignore
          }
        }, "close-consumer"));
      }
    }
  }

  public void resetOffsets() {
    LOG.info("Resetting consumer offsets.");
    ensureConsumer();
    // some of this might not be required but it took me a while to even get it to reset at all
    consumer.subscribe(Arrays.asList(topic));
    // throw away results to force assignment
    consumer.poll(1);
    consumer.seekToBeginning(consumer.assignment());
    consumer.assignment().forEach(tp -> consumer.seek(tp, 0));
    consumer.poll(TimeUnit.SECONDS.toMillis(1));
    consumer.assignment().forEach(tp -> consumer.seek(tp, 0));
  }

  @Override
  public void run() {
    ensureConsumer();
    consumer.subscribe(Arrays.asList(topic));

    int consecutiveNoData = 0;
    long cumulativeTotal = 0;
    long lastReport = 0;
    while (!shutdown.get()) {
      ensureConsumer();
      // TODO configure?
      final ConsumerRecords<String, String> records = consumer.poll(TimeUnit.SECONDS.toMillis(1));
      LOG.debug(String.format("Got %d records.", records.count()));

      cumulativeTotal += records.count();
      if (records.isEmpty()) {
        consecutiveNoData++;
        if (CONSECUTIVE_NO_DATA_LOG_COUNT == consecutiveNoData) {
          // only log this once
          LOG.info("No data in last " + CONSECUTIVE_NO_DATA_LOG_COUNT + " polls.");
        }
      } else {
        consecutiveNoData = 0;
      }
      if (cumulativeTotal - lastReport >= 1000) {
        LOG.info(String.format("Have processed %d records.", cumulativeTotal));
        lastReport = cumulativeTotal;
      }

      records.forEach(record -> {
        try {
          destination.save(mapper.readValue(record.value(), Event.class));
        } catch (final Exception e) {
          LOG.error(String.format("Unable to save event topic=%s partition=%d offset=%d value=%s",
              record.topic(), record.partition(), record.offset(), record.value()), e);
          return;
        }
      });
    }
  }
}
