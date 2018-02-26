/**
 * Copyright (c) 2017-2018, Andy Janata
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

package net.socialgamer.pyx.metrics.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.inject.Inject;


@JsonDeserialize(using = Event.Deserializer.class)
public class Event {
  final private long timestamp;
  final private String build;
  final private String type;
  final private EventData data;
  final private String version;

  public Event(final long timestamp, final String build, final String type, final EventData data,
      final String version) {
    this.timestamp = timestamp;
    this.build = build;
    this.type = type;
    this.data = data;
    this.version = version;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getBuild() {
    return build;
  }

  public String getType() {
    return type;
  }

  public EventData getData() {
    return data;
  }

  public String getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return String.format("%s[ts=%d, type=%s, data=[%s], build=%s, version=%s]",
        getClass().getSimpleName(), timestamp, type, data, build, version);
  }

  public static class Deserializer extends StdDeserializer<Event> {

    private static final long serialVersionUID = 830785475924899142L;

    @Inject
    static ObjectMapper mapper;

    protected Deserializer() {
      this(null);
    }

    protected Deserializer(final Class<?> vc) {
      super(vc);
    }

    @Override
    public Event deserialize(final JsonParser p, final DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      final JsonNode node = p.getCodec().readTree(p);
      final long timestamp = node.get("timestamp").asLong();
      final String build = node.get("build").asText();
      final String version = node.get("version").asText();
      final String type = node.get("type").asText();
      final JsonNode dataObj = node.get("data");

      // TODO better
      if (!"0.1".equals(version) && !"0.2".equals(version)) {
        ctxt.reportMappingException("Unknown event version " + version);
        return null;
      }

      final EventData data;
      switch (type) {
        case "serverStart":
          data = mapper.readValue(dataObj.traverse(), ServerStart.class);
          break;
        case "userConnect":
          data = mapper.readValue(dataObj.traverse(), UserConnect.class);
          break;
        case "userDisconnect":
          data = mapper.readValue(dataObj.traverse(), UserDisconnect.class);
          break;
        case "gameStart":
          data = mapper.readValue(dataObj.traverse(), GameStart.class);
          break;
        case "roundComplete":
          data = mapper.readValue(dataObj.traverse(), RoundComplete.class);
          break;
        case "cardDealt":
          data = mapper.readValue(dataObj.traverse(), CardDealt.class);
          break;
        default:
          ctxt.reportMappingException("Unknown event type " + type);
          return null;
      }

      return new Event(timestamp, build, type, data, version);
    }
  }
}
