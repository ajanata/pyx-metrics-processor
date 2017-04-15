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

package net.socialgamer.pyx.metrics.data;

import java.util.List;


public class GameStart extends EventData {
  private String gameId;
  private int blankCardsInDeck;
  private int maxPlayers;
  private int scoreGoal;
  private boolean hasPassword;
  private List<DeckInfo> decks;

  public String getGameId() {
    return gameId;
  }

  public void setGameId(final String gameId) {
    this.gameId = gameId;
  }

  public int getBlankCardsInDeck() {
    return blankCardsInDeck;
  }

  public void setBlankCardsInDeck(final int blankCardsInDeck) {
    this.blankCardsInDeck = blankCardsInDeck;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public void setMaxPlayers(final int maxPlayers) {
    this.maxPlayers = maxPlayers;
  }

  public int getScoreGoal() {
    return scoreGoal;
  }

  public void setScoreGoal(final int scoreGoal) {
    this.scoreGoal = scoreGoal;
  }

  public boolean isHasPassword() {
    return hasPassword;
  }

  public void setHasPassword(final boolean hasPassword) {
    this.hasPassword = hasPassword;
  }

  public List<DeckInfo> getDecks() {
    return decks;
  }

  public void setDecks(final List<DeckInfo> decks) {
    this.decks = decks;
  }

  @Override
  public String toString() {
    return String.format("%s[id=%s, blanks=%d, max=%d, goal=%d, pass=%s, decks=[%s]]",
        getClass().getSimpleName(), gameId, blankCardsInDeck, maxPlayers, scoreGoal, hasPassword,
        decks);
  }

  public static class DeckInfo {
    private boolean isCustom;
    private int id;
    private String name;
    private int whiteCount;
    private int blackCount;

    public boolean isCustom() {
      return isCustom;
    }

    public void setIsCustom(final boolean isCustom) {
      this.isCustom = isCustom;
    }

    public int getId() {
      return id;
    }

    public void setId(final int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public int getWhiteCount() {
      return whiteCount;
    }

    public void setWhiteCount(final int whiteCount) {
      this.whiteCount = whiteCount;
    }

    public int getBlackCount() {
      return blackCount;
    }

    public void setBlackCount(final int blackCount) {
      this.blackCount = blackCount;
    }

    @Override
    public String toString() {
      return String.format("%s[id=%d, name=%s, custom=%s, whites=%d, blacks=%d]",
          getClass().getSimpleName(), id, name, isCustom, whiteCount, blackCount);
    }
  }
}
