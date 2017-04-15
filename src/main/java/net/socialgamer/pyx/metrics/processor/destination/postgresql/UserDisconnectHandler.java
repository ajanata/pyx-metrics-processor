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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import net.socialgamer.pyx.metrics.data.Event;
import net.socialgamer.pyx.metrics.data.UserDisconnect;

import org.apache.commons.lang3.StringUtils;


public class UserDisconnectHandler implements EventHandler {

  private final PreparedStatement stmt;

  public UserDisconnectHandler(final Connection connection) throws SQLException {
    stmt = connection.prepareStatement("INSERT INTO user_session_end"
        + " (session_id, meta)"
        + " VALUES (?, ROW(?, ?))"
        + " ON CONFLICT DO NOTHING", Statement.RETURN_GENERATED_KEYS);
  }

  @Override
  public void close() throws Exception {
    stmt.close();
  }

  @Override
  public void handle(final Event event) throws SQLException {
    stmt.clearParameters();
    final UserDisconnect data = (UserDisconnect) event.getData();
    stmt.setString(1, StringUtils.left(data.getSessionId(), 100));
    stmt.setTimestamp(2, new Timestamp(event.getTimestamp()));
    stmt.setString(3, StringUtils.left(event.getBuild(), 100));
    stmt.executeUpdate();
  }
}
