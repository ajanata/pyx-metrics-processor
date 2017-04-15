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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.socialgamer.pyx.metrics.data.Event;
import net.socialgamer.pyx.metrics.data.RoundComplete;
import net.socialgamer.pyx.metrics.data.RoundComplete.BlackCardInfo;
import net.socialgamer.pyx.metrics.data.RoundComplete.WhiteCardInfo;

import org.apache.commons.lang3.StringUtils;


public class RoundCompleteHandler implements EventHandler {

  private final PreparedStatement getBlackStmt;
  private final PreparedStatement makeBlackStmt;
  private final PreparedStatement getWhiteStmt;
  private final PreparedStatement makeWhiteStmt;
  private final PreparedStatement getRoundStmt;
  private final PreparedStatement makeRoundStmt;
  private final PreparedStatement joinStmt;

  public RoundCompleteHandler(final Connection connection) throws SQLException {
    getBlackStmt = connection.prepareStatement("SELECT uid FROM black_card"
        + " WHERE text = ? AND is_custom = ? AND watermark = ? AND draw = ? AND pick = ?");
    makeBlackStmt = connection.prepareStatement("INSERT INTO black_card"
        + " (text, is_custom, watermark, draw, pick)"
        + " VALUES (?, ?, ?, ?, ?)"
        + " ON CONFLICT DO NOTHING");
    getWhiteStmt = connection.prepareStatement("SELECT uid FROM white_card"
        + " WHERE text = ? AND is_custom = ? AND is_write_in = ? AND watermark = ?");
    makeWhiteStmt = connection.prepareStatement("INSERT INTO white_card"
        + " (text, is_custom, is_write_in, watermark)"
        + " VALUES (?, ?, ?, ?)"
        + " ON CONFLICT DO NOTHING");
    getRoundStmt = connection.prepareStatement("SELECT uid FROM round_complete"
        + " WHERE game_id = ? AND round_id = ? AND judge_session_id = ? AND winner_session_id = ?"
        + " AND black_card_uid = ? AND (meta).timestamp = ? AND (meta).build = ?");
    makeRoundStmt = connection
        .prepareStatement("INSERT INTO round_complete"
            + " (game_id, round_id, judge_session_id, winner_session_id, black_card_uid, has_any_non_stock, meta)"
            + " VALUES (?, ?, ?, ?, ?, ?, ROW(?, ?))"
            + " ON CONFLICT DO NOTHING");
    joinStmt = connection.prepareStatement("INSERT INTO round_complete__user_session__white_card"
        + " (round_complete_uid, session_id, white_card_uid, white_card_index)"
        + " VALUES (?, ?, ?, ?)"
        + " ON CONFLICT DO NOTHING");
  }

  @Override
  public void close() throws Exception {
    getBlackStmt.close();
    makeBlackStmt.close();
    getWhiteStmt.close();
    makeWhiteStmt.close();
    getRoundStmt.close();
    makeRoundStmt.close();
    joinStmt.close();
  }

  private String notNull(final String s) {
    if (null == s) {
      return "";
    } else {
      return s;
    }
  }

  /**
   * @param card BlackCardInfo object
   * @return The card's UID, or -1 if the card does not already exist
   * @throws SQLException If the query has a problem
   */
  private long getBlackUid(final BlackCardInfo card) throws SQLException {
    getBlackStmt.clearParameters();
    getBlackStmt.setString(1, StringUtils.left(card.getText(), 1000));
    getBlackStmt.setBoolean(2, card.isCustom());
    getBlackStmt.setString(3, StringUtils.left(notNull(card.getWatermark()), 20));
    getBlackStmt.setInt(4, card.getDraw());
    getBlackStmt.setInt(5, card.getPick());
    try (ResultSet rs = getBlackStmt.executeQuery()) {
      if (rs.next()) {
        return rs.getLong(1);
      } else {
        return -1;
      }
    }
  }

  /**
   * Gets existing black card or inserts into database
   * @param card BlackCardInfo object
   * @return The UID of the black card, whether or not it already existed or was just inserted
   * @throws SQLException If the query has a problem
   */
  private long getOrMakeBlack(final BlackCardInfo card) throws SQLException {
    long currUid = getBlackUid(card);
    if (-1 == currUid) {
      makeBlackStmt.clearParameters();
      makeBlackStmt.setString(1, StringUtils.left(card.getText(), 1000));
      makeBlackStmt.setBoolean(2, card.isCustom());
      makeBlackStmt.setString(3, StringUtils.left(notNull(card.getWatermark()), 20));
      makeBlackStmt.setInt(4, card.getDraw());
      makeBlackStmt.setInt(5, card.getPick());
      makeBlackStmt.executeUpdate();

      currUid = getBlackUid(card);
      if (-1 == currUid) {
        // this should be unreachable
        throw new CreatedRowNotFoundException(String.format("Created black card not found: %s",
            card));
      }
    }
    return currUid;
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
    getWhiteStmt.setString(4, StringUtils.left(notNull(card.getWatermark()), 20));
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

  /**
   * @param event Event object
   * @param blackUid UID of black card for the round
   * @return The UID of the round, whether or not it already existed or was just inserted
   * @throws SQLException If the query has a problem
   */
  private long getRound(final Event event, final long blackUid) throws SQLException {
    final RoundComplete data = (RoundComplete) event.getData();
    getRoundStmt.clearParameters();
    getRoundStmt.setString(1, StringUtils.left(data.getGameId(), 100));
    getRoundStmt.setString(2, StringUtils.left(data.getRoundId(), 100));
    getRoundStmt.setString(3, StringUtils.left(data.getJudgeSessionId(), 100));
    getRoundStmt.setString(4, StringUtils.left(data.getWinnerSessionId(), 100));
    getRoundStmt.setLong(5, blackUid);
    getRoundStmt.setTimestamp(6, new Timestamp(event.getTimestamp()));
    getRoundStmt.setString(7, StringUtils.left(event.getBuild(), 100));
    try (ResultSet rs = getRoundStmt.executeQuery()) {
      if (rs.next()) {
        return rs.getLong(1);
      } else {
        return -1;
      }
    }
  }

  /**
   * Gets existing round or inserts into database
   * @param event Event object
   * @param blackUid UID of black card for the round
   * @return The UID of the round, whether or not it already existed or was just inserted
   * @throws SQLException If the query has a problem
   */
  private long getOrMakeRound(final Event event, final long blackUid, final boolean hasAnyNonStock)
      throws SQLException {
    long currUid = getRound(event, blackUid);
    if (-1 == currUid) {
      final RoundComplete data = (RoundComplete) event.getData();
      makeRoundStmt.clearParameters();
      makeRoundStmt.setString(1, StringUtils.left(data.getGameId(), 100));
      makeRoundStmt.setString(2, StringUtils.left(data.getRoundId(), 100));
      makeRoundStmt.setString(3, StringUtils.left(data.getJudgeSessionId(), 100));
      makeRoundStmt.setString(4, StringUtils.left(data.getWinnerSessionId(), 100));
      makeRoundStmt.setLong(5, blackUid);
      makeRoundStmt.setBoolean(6, hasAnyNonStock);
      makeRoundStmt.setTimestamp(7, new Timestamp(event.getTimestamp()));
      makeRoundStmt.setString(8, StringUtils.left(event.getBuild(), 100));
      makeRoundStmt.executeUpdate();

      currUid = getRound(event, blackUid);
      if (-1 == currUid) {
        // this should be unreachable
        throw new CreatedRowNotFoundException(String.format("Created round not found: %s", event));
      }
    }
    return currUid;
  }

  @Override
  public void handle(final Event event) throws SQLException {
    final RoundComplete data = (RoundComplete) event.getData();

    final long blackUid = getOrMakeBlack(data.getBlackCard());

    boolean hasAnyNonStock = data.getBlackCard().isCustom();
    // scan for any white cards that are custom or write in
    final Iterator<Entry<String, List<WhiteCardInfo>>> iter = data.getCardsByUserId().entrySet()
        .iterator();
    while (!hasAnyNonStock && iter.hasNext()) {
      final Entry<String, List<WhiteCardInfo>> entry = iter.next();
      final Iterator<WhiteCardInfo> cardIter = entry.getValue().iterator();
      while (!hasAnyNonStock && cardIter.hasNext()) {
        final WhiteCardInfo card = cardIter.next();
        hasAnyNonStock |= card.isCustom() || card.isWriteIn();
      }
    }

    final long roundUid = getOrMakeRound(event, blackUid, hasAnyNonStock);

    // convert and store all of the white cards in one pass
    for (final Entry<String, List<WhiteCardInfo>> played : data.getCardsByUserId().entrySet()) {
      final List<WhiteCardInfo> cards = played.getValue();
      final String sessionId = StringUtils.left(played.getKey(), 100);
      for (int i = 0; i < cards.size(); i++) {
        joinStmt.clearParameters();
        joinStmt.setLong(1, roundUid);
        joinStmt.setString(2, sessionId);
        joinStmt.setLong(3, getOrMakeWhite(cards.get(i)));
        joinStmt.setInt(4, i);
        joinStmt.executeUpdate();
      }
    }
  }
}
