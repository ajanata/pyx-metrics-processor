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
import java.util.HashSet;
import java.util.Set;

import net.socialgamer.pyx.metrics.data.Event;
import net.socialgamer.pyx.metrics.data.GameStart;
import net.socialgamer.pyx.metrics.data.GameStart.DeckInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public class GameStartHandler implements EventHandler {

  private static final Logger LOG = Logger.getLogger(GameStartHandler.class);

  private final boolean includeCustomCards;
  private final PreparedStatement getDeckStmt;
  private final PreparedStatement makeDeckStmt;
  private final PreparedStatement gameStmt;
  private final PreparedStatement gameDeckStmt;

  public GameStartHandler(final Connection connection, final boolean includeCustomCards)
      throws SQLException {
    gameStmt = connection.prepareStatement("INSERT INTO game_start"
        + " (game_id, blanks_in_deck, max_players, score_goal, has_password, meta)"
        + " VALUES (?, ?, ?, ?, ?, ROW(?, ?))"
        + " ON CONFLICT DO NOTHING");
    getDeckStmt = connection.prepareStatement("SELECT uid FROM deck"
        + " WHERE name = ? AND is_custom = ? AND id = ? AND white_count = ? AND black_count = ?");
    makeDeckStmt = connection.prepareStatement("INSERT INTO deck"
        + " (name, is_custom, id, white_count, black_count)"
        + " VALUES (?, ?, ?, ?, ?)"
        + " ON CONFLICT DO NOTHING");
    gameDeckStmt = connection.prepareStatement("INSERT INTO game_start__deck"
        + " (game_id, deck_uid)"
        + " VALUES (?, ?)"
        + " ON CONFLICT DO NOTHING");
    this.includeCustomCards = includeCustomCards;
  }

  @Override
  public void close() throws Exception {
    getDeckStmt.close();
    makeDeckStmt.close();
    gameStmt.close();
    gameDeckStmt.close();
  }

  /**
   * @param deck DeckInfo object
   * @return The deck's UID, or -1 if the deck does not already exist
   * @throws SQLException If the query has a problem
   */
  private long getDeckUid(final DeckInfo deck) throws SQLException {
    getDeckStmt.clearParameters();
    getDeckStmt.setString(1, StringUtils.left(deck.getName(), 1000));
    getDeckStmt.setBoolean(2, deck.isCustom());
    getDeckStmt.setInt(3, deck.getId());
    getDeckStmt.setInt(4, deck.getWhiteCount());
    getDeckStmt.setInt(5, deck.getBlackCount());
    try (ResultSet rs = getDeckStmt.executeQuery()) {
      if (rs.next()) {
        return rs.getLong(1);
      } else {
        return -1;
      }
    }
  }

  @Override
  public void handle(final Event event) throws SQLException {
    final GameStart data = (GameStart) event.getData();

    final Set<Long> deckUids = new HashSet<>();
    // see if decks exist and create if they don't
    for (final DeckInfo deck : data.getDecks()) {
      if (!includeCustomCards && deck.isCustom()) {
        LOG.debug(String.format("Skipping custom deck %s for game %s", deck.getName(),
            data.getGameId()));
        continue;
      }
      long currUid = getDeckUid(deck);
      if (-1 == currUid) {
        // add deck, get ID again
        makeDeckStmt.clearParameters();
        makeDeckStmt.setString(1, StringUtils.left(deck.getName(), 1000));
        makeDeckStmt.setBoolean(2, deck.isCustom());
        makeDeckStmt.setInt(3, deck.getId());
        makeDeckStmt.setInt(4, deck.getWhiteCount());
        makeDeckStmt.setInt(5, deck.getBlackCount());
        makeDeckStmt.executeUpdate();

        currUid = getDeckUid(deck);
        if (-1 == currUid) {
          // this should be unreachable
          throw new CreatedRowNotFoundException(String.format("Created deck not found: %s", deck));
        }
      }
      deckUids.add(currUid);
    }
    if (deckUids.isEmpty()) {
      LOG.debug(String.format("Game %s contains only custom decks, skipping.", data.getGameId()));
      return;
    }

    // store the game itself
    gameStmt.clearParameters();
    gameStmt.setString(1, StringUtils.left(data.getGameId(), 100));
    gameStmt.setInt(2, data.getBlankCardsInDeck());
    gameStmt.setInt(3, data.getMaxPlayers());
    gameStmt.setInt(4, data.getScoreGoal());
    gameStmt.setBoolean(5, data.isHasPassword());
    gameStmt.setTimestamp(6, new Timestamp(event.getTimestamp()));
    gameStmt.setString(7, StringUtils.left(event.getBuild(), 100));
    gameStmt.executeUpdate();

    // now store the decks
    for (final long deckUid : deckUids) {
      gameDeckStmt.clearParameters();
      gameDeckStmt.setString(1, StringUtils.left(data.getGameId(), 100));
      gameDeckStmt.setLong(2, deckUid);
      gameDeckStmt.executeUpdate();
    }
  }
}
