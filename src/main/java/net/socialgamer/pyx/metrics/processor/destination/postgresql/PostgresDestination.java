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

package net.socialgamer.pyx.metrics.processor.destination.postgresql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.socialgamer.pyx.metrics.data.Event;
import net.socialgamer.pyx.metrics.data.EventData;
import net.socialgamer.pyx.metrics.data.GameStart;
import net.socialgamer.pyx.metrics.data.RoundComplete;
import net.socialgamer.pyx.metrics.data.ServerStart;
import net.socialgamer.pyx.metrics.data.UserConnect;
import net.socialgamer.pyx.metrics.data.UserDisconnect;
import net.socialgamer.pyx.metrics.inject.ProcessorModule.JdbcConnectionProvider;
import net.socialgamer.pyx.metrics.processor.destination.Destination;

import org.apache.log4j.Logger;

import com.google.inject.Inject;


public class PostgresDestination implements Destination {

  private static final Logger LOG = Logger.getLogger(PostgresDestination.class);

  private final JdbcConnectionProvider<Connection> connectionProvider;
  private Connection connection;
  private final Map<Class<? extends EventData>, EventHandler> handlers = new HashMap<>();

  @Inject
  public PostgresDestination(final JdbcConnectionProvider<Connection> connectionProvider) {
    this.connectionProvider = connectionProvider;
  }

  private synchronized void ensureConnection() {
    if (null == connection) {
      LOG.info("Creating new database connection.");
      try {
        connection = connectionProvider.get();
        connection.setAutoCommit(false);
      } catch (final SQLException | ClassNotFoundException e) {
        LOG.error("Unable to connect to Postgres.", e);
        // TODO retry?
        // FIXME this really shouldn't do this here
        System.exit(2);
        return;
      }
      final Connection connectionForHook = connection;
      if (null != connectionForHook) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          LOG.info("Closing database connection.");
          // TODO this doesn't seem like the right place?
          closeExistingHandlers();
          try {
            LOG.info("Waiting 1 second for existing queries to complete.");
            // wait a moment to hopefully let anything that was going on finish
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            LOG.info("Rolling back transaction and closing connection.");
            try {
              connectionForHook.rollback();
            } catch (final Exception ee) {
              // ignore
            }
            connectionForHook.close();
          } catch (final Exception e) {
            // ignore
          }
        }, "close-connection"));
      }
      try {
        createHandlers();
      } catch (final SQLException e) {
        LOG.error("Unable to prepare statements.", e);
        // TODO retry?
        // FIXME this really shouldn't do this here
        System.exit(2);
      }
    }
  }

  private synchronized void createHandlers() throws SQLException {
    if (!handlers.isEmpty()) {
      closeExistingHandlers();
    }

    LOG.info("Creating handlers.");
    // FIXME this is ugly. inject somehow?
    handlers.put(ServerStart.class, new ServerStartHandler(connection));
    handlers.put(UserDisconnect.class, new UserDisconnectHandler(connection));
    handlers.put(UserConnect.class, new UserConnectHandler(connection));
    handlers.put(GameStart.class, new GameStartHandler(connection));
    handlers.put(RoundComplete.class, new RoundCompleteHandler(connection));
  }

  private synchronized void closeExistingHandlers() {
    LOG.info("Closing existing handlers.");
    handlers.values().forEach(handler -> {
      try {
        handler.close();
      } catch (final Exception e) {
        // ignore
      }
    });
    handlers.clear();
  }

  @Override
  public void save(final Event event) throws SQLException {
    ensureConnection();
    final EventHandler handler = handlers.get(event.getData().getClass());
    if (null == handler) {
      LOG.warn(String.format("Unknown event type %s!", event.getData().getClass().getSimpleName()));
    } else {
      try {
        handler.handle(event);
        connection.commit();
      } catch (final SQLException e) {
        LOG.error("Unable to save event, closing connection.", e);
        try {
          connection.rollback();
        } catch (final SQLException ee) {
          // ignore
        }
        closeExistingHandlers();
        try {
          connection.close();
        } catch (final SQLException ee) {
          // ignore
        }
        connection = null;
        throw e;
      }
    }
  }
}
