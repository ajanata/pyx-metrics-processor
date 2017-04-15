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

package net.socialgamer.pyx.metrics.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import net.socialgamer.pyx.metrics.data.Event;
import net.socialgamer.pyx.metrics.processor.destination.Destination;
import net.socialgamer.pyx.metrics.processor.destination.postgresql.PostgresDestination;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.CheckedProvider;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;


public class ProcessorModule extends AbstractModule {

  private final Properties props;

  public ProcessorModule(final Properties props) {
    this.props = props;
  }

  @Override
  protected void configure() {
    install(ThrowingProviderBinder.forModule(this));

    Names.bindProperties(binder(), props);

    bind(ObjectMapper.class).toInstance(new ObjectMapper());
    requestStaticInjection(Event.Deserializer.class);

    // TODO move to configuration file?
    bind(Destination.class).to(PostgresDestination.class);
  }

  @Provides
  @ConsumerProperties
  @Singleton
  Properties provideConsumerProperties(@Named("kafka.host") final String host,
      @Named("kafka.consumer.group") final String consumerGroup,
      @Named("kafka.ssl") final String ssl,
      @Named("kafka.truststore.path") final String truststorePath,
      @Named("kafka.truststore.password") final String truststorePassword,
      @Named("kafka.ssl.usekey") final String useKey,
      @Named("kafka.keystore.path") final String keystorePath,
      @Named("kafka.keystore.password") final String keystorePassword,
      @Named("kafka.key.password") final String keyPassword,
      @Named("kafka.sasl") final String sasl,
      @Named("kafka.sasl.username") final String saslUsername,
      @Named("kafka.sasl.password") final String saslPassword) {
    final Properties consumerProps = new Properties();
    consumerProps.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, host);
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    consumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "pyx-analytics-processor");
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
    // TODO configurable?
    consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, (int) TimeUnit.SECONDS.toMillis(6));
    consumerProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, (int) TimeUnit.SECONDS.toMillis(6));
    consumerProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG,
        (int) TimeUnit.SECONDS.toMillis(10));
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    if (Boolean.valueOf(ssl)) {
      consumerProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
      consumerProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststorePath);
      consumerProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);

      if (Boolean.valueOf(useKey)) {
        consumerProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystorePath);
        consumerProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
        consumerProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, keyPassword);
      }

      if (Boolean.valueOf(sasl)) {
        consumerProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        consumerProps.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
            "org.apache.kafka.common.security.scram.ScramLoginModule "
                + "required \n username=\"%s\" \n password=\"%s\";",
            saslUsername, saslPassword));
        consumerProps.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
      }
    }
    return consumerProps;
  }

  @Provides
  KafkaConsumer<String, String> provideConsumer(@ConsumerProperties final Properties consumerProps) {
    return new KafkaConsumer<String, String>(consumerProps);
  }

  @Singleton
  @Provides
  @PostgresProperties
  Properties providePostgresProperties(@Named("postgres.username") final String username,
      @Named("postgres.password") final String password) {
    final Properties postgresProps = new Properties();
    // TODO ssl
    postgresProps.setProperty("user", username);
    postgresProps.setProperty("password", password);
    return postgresProps;
  }

  @CheckedProvides(JdbcConnectionProvider.class)
  Connection providePostgresConnection(@PostgresProperties final Properties postgresProps,
      @Named("postgres.url") final String url) throws SQLException, ClassNotFoundException {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection(url, postgresProps);
  }

  public interface JdbcConnectionProvider<T> extends CheckedProvider<T> {
    @Override
    T get() throws SQLException, ClassNotFoundException;
  }

  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ConsumerProperties {
  }

  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  public @interface PostgresProperties {
  }
}
