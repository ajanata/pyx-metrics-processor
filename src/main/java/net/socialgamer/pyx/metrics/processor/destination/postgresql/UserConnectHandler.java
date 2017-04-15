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
import java.util.List;

import net.socialgamer.pyx.metrics.data.Event;
import net.socialgamer.pyx.metrics.data.UserConnect;

import org.apache.commons.lang3.StringUtils;


public class UserConnectHandler implements EventHandler {

  private final Connection conn;
  private final PreparedStatement stmt;

  public UserConnectHandler(final Connection connection) throws SQLException {
    conn = connection;
    stmt = connection.prepareStatement("INSERT INTO user_session"
        + " (persistent_id, session_id, geo, browser, meta)"
        + " VALUES (?, ?, ROW(?, ?, ?, ?, ?), ROW(?, ?, ?, ?), ROW(?, ?))"
        + " ON CONFLICT DO NOTHING", Statement.RETURN_GENERATED_KEYS);
  }

  @Override
  public void close() throws Exception {
    stmt.close();
  }

  @Override
  public void handle(final Event event) throws SQLException {
    stmt.clearParameters();

    final UserConnect data = (UserConnect) event.getData();
    stmt.setString(1, StringUtils.left(data.getPersistentId(), 100));
    stmt.setString(2, StringUtils.left(data.getSessionId(), 100));
    // Geo
    stmt.setString(3, StringUtils.left(data.getGeo().getCity(), 100));
    stmt.setString(4, StringUtils.left(data.getGeo().getCountry(), 100));
    stmt.setString(5, StringUtils.left(data.getGeo().getRepresentedCountry(), 100));
    Object[] subdivs = new Object[0];
    final List<String> subdivOrig = data.getGeo().getSubdivisions();
    if (null != subdivOrig) {
      subdivs = new Object[subdivOrig.size()];
      for (int i = 0; i < subdivOrig.size(); i++) {
        subdivs[i] = StringUtils.left(subdivOrig.get(i), 100);
      }
    }
    stmt.setArray(6, conn.createArrayOf("TEXT", subdivs));
    stmt.setString(7, StringUtils.left(data.getGeo().getPostal(), 100));
    // Browser
    stmt.setString(8, StringUtils.left(data.getBrowser().getName(), 100));
    stmt.setString(9, StringUtils.left(data.getBrowser().getType(), 100));
    stmt.setString(10, StringUtils.left(data.getBrowser().getOs(), 100));
    stmt.setString(11, StringUtils.left(data.getBrowser().getLanguage(), 100));
    // Meta
    stmt.setTimestamp(12, new Timestamp(event.getTimestamp()));
    stmt.setString(13, StringUtils.left(event.getBuild(), 100));

    stmt.executeUpdate();
  }
}
