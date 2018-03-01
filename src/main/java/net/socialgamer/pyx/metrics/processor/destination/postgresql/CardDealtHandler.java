/**
 * Copyright (c) 2018, Andy Janata
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import net.socialgamer.pyx.metrics.data.CardDealt;
import net.socialgamer.pyx.metrics.data.Event;
import net.socialgamer.pyx.metrics.data.WhiteCardInfo;


public class CardDealtHandler implements EventHandler {

  private static final Logger LOG = Logger.getLogger(CardDealtHandler.class);

  private final boolean includeCustomCards;
  private final PreparedStatement getWhiteStmt;
  private final PreparedStatement makeWhiteStmt;
  private final PreparedStatement makeDealtStmt;

  public CardDealtHandler(final Connection connection, final boolean includeCustomCards)
      throws SQLException {
    getWhiteStmt = connection.prepareStatement("SELECT uid FROM white_card"
        + " WHERE text = ? AND is_custom = ? AND is_write_in = ?");
    makeWhiteStmt = connection.prepareStatement("INSERT INTO white_card"
        + " (text, is_custom, is_write_in, watermark)"
        + " VALUES (?, ?, ?, ?)"
        + " ON CONFLICT DO NOTHING");
    makeDealtStmt = connection
        .prepareStatement("INSERT INTO card_dealt"
            + " (game_id, session_id, white_card_uid, deal_seq, meta)"
            + " VALUES (?, ?, ?, ?, ROW(?, ?))"
            + " ON CONFLICT DO NOTHING");

    this.includeCustomCards = includeCustomCards;
  }

  @Override
  public void close() throws Exception {
    getWhiteStmt.close();
    makeWhiteStmt.close();
    makeDealtStmt.close();
  }

  private String notNull(final String s) {
    if (null == s) {
      return "";
    } else {
      return s;
    }
  }

  /**
   * @param card WhiteCardInfo object
   * @return The card's UID, or -1 if the card does not already exist
   * @throws SQLException If the query has a problem
   */
  private long getWhiteUid(final WhiteCardInfo card) throws SQLException {
    getWhiteStmt.clearParameters();
    getWhiteStmt.setString(1, StringUtils.left(card.getText(), 1000));
    getWhiteStmt.setBoolean(2, card.isCustom());
    getWhiteStmt.setBoolean(3, card.isWriteIn());
    try (ResultSet rs = getWhiteStmt.executeQuery()) {
      if (rs.next()) {
        return rs.getLong(1);
      } else {
        return -1;
      }
    }
  }

  /**
   * Gets existing white card or inserts into database
   * @param card WhiteCardInfo object
   * @return The UID of the white card, whether or not it already existed or was just inserted
   * @throws SQLException If the query has a problem
   */
  private long getOrMakeWhite(final WhiteCardInfo card) throws SQLException {
    long currUid = getWhiteUid(card);
    if (-1 == currUid) {
      makeWhiteStmt.clearParameters();
      makeWhiteStmt.setString(1, StringUtils.left(card.getText(), 1000));
      makeWhiteStmt.setBoolean(2, card.isCustom());
      makeWhiteStmt.setBoolean(3, card.isWriteIn());
      makeWhiteStmt.setString(4, StringUtils.left(notNull(card.getWatermark()), 20));
      makeWhiteStmt.executeUpdate();

      currUid = getWhiteUid(card);
      if (-1 == currUid) {
        // this should be unreachable
        throw new CreatedRowNotFoundException(String.format("Created white card not found: %s",
            card));
      }
    }
    return currUid;
  }

  @Override
  public void handle(final Event event) throws SQLException {
    final CardDealt data = (CardDealt) event.getData();
    final WhiteCardInfo card = data.getCard();

    if (!includeCustomCards && (card.isCustom() || card.isWriteIn())) {
      LOG.debug(String.format("Card %s is custom, skipping.", card));
      return;
    }

    final long cardUid = getOrMakeWhite(card);

    makeDealtStmt.clearParameters();
    makeDealtStmt.setString(1, data.getGameId());
    makeDealtStmt.setString(2, data.getSessionId());
    makeDealtStmt.setLong(3, cardUid);
    makeDealtStmt.setLong(4, data.getDealSeq());
    makeDealtStmt.setTimestamp(5, new Timestamp(event.getTimestamp()));
    makeDealtStmt.setString(6, StringUtils.left(event.getBuild(), 100));
    makeDealtStmt.executeUpdate();
  }

}
