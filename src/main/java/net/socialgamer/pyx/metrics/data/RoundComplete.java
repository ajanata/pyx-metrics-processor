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
import java.util.Map;


public class RoundComplete extends EventData {
  private String gameId;
  private String roundId;
  private String judgeSessionId;
  private String winnerSessionId;
  private BlackCardInfo blackCard;
  private Map<String, List<WhiteCardInfo>> cardsByUserId;

  public String getGameId() {
    return gameId;
  }

  public void setGameId(final String gameId) {
    this.gameId = gameId;
  }

  public String getRoundId() {
    return roundId;
  }

  public void setRoundId(final String roundId) {
    this.roundId = roundId;
  }

  public String getJudgeSessionId() {
    return judgeSessionId;
  }

  public void setJudgeSessionId(final String judgeSessionId) {
    this.judgeSessionId = judgeSessionId;
  }

  public String getWinnerSessionId() {
    return winnerSessionId;
  }

  public void setWinnerSessionId(final String winnerSessionId) {
    this.winnerSessionId = winnerSessionId;
  }

  public BlackCardInfo getBlackCard() {
    return blackCard;
  }

  public void setBlackCard(final BlackCardInfo blackCard) {
    this.blackCard = blackCard;
  }

  public Map<String, List<WhiteCardInfo>> getCardsByUserId() {
    return cardsByUserId;
  }

  public void setCardsByUserId(final Map<String, List<WhiteCardInfo>> cardsByUserId) {
    this.cardsByUserId = cardsByUserId;
  }

  @Override
  public String toString() {
    return String.format("%s[id=%s, round=%s, judge=%s, winner=%s, black=%s, whites=[%s]]",
        getClass().getSimpleName(), gameId, roundId, judgeSessionId, winnerSessionId, blackCard,
        cardsByUserId);
  }

  public static class WhiteCardInfo {
    private boolean isCustom;
    private boolean isWriteIn;
    private int id;
    private String watermark;
    private String text;

    public boolean isCustom() {
      return isCustom;
    }

    public void setIsCustom(final boolean isCustom) {
      this.isCustom = isCustom;
    }

    public boolean isWriteIn() {
      return isWriteIn;
    }

    public void setIsWriteIn(final boolean isWriteIn) {
      this.isWriteIn = isWriteIn;
    }

    public int getId() {
      return id;
    }

    public void setId(final int id) {
      this.id = id;
    }

    public String getWatermark() {
      return watermark;
    }

    public void setWatermark(final String watermark) {
      this.watermark = watermark;
    }

    public String getText() {
      return text;
    }

    public void setText(final String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return String.format("%s[id=%d, watermark=%s, text=%s, custom=%s, blank=%s]",
          getClass().getSimpleName(), id, watermark, text, isCustom, isWriteIn);
    }
  }

  public static class BlackCardInfo {
    private boolean isCustom;
    private int id;
    private String watermark;
    private String text;
    private int draw;
    private int pick;

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

    public String getWatermark() {
      return watermark;
    }

    public void setWatermark(final String watermark) {
      this.watermark = watermark;
    }

    public String getText() {
      return text;
    }

    public void setText(final String text) {
      this.text = text;
    }

    public int getDraw() {
      return draw;
    }

    public void setDraw(final int draw) {
      this.draw = draw;
    }

    public int getPick() {
      return pick;
    }

    public void setPick(final int pick) {
      this.pick = pick;
    }

    @Override
    public String toString() {
      return String.format("%s[id=%d, watermark=%s, text=%s, custom=%s, draw=%d, pick=%d]",
          getClass().getSimpleName(), id, watermark, text, isCustom, draw, pick);
    }
  }
}
