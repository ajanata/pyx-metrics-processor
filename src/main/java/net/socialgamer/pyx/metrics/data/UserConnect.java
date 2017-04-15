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


public class UserConnect extends EventData {
  private String persistentId;
  private String sessionId;
  private BrowserInfo browser;
  private GeoInfo geo;

  public String getPersistentId() {
    return persistentId;
  }

  public void setPersistentId(final String persistentId) {
    this.persistentId = persistentId;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(final String sessionId) {
    this.sessionId = sessionId;
  }

  public BrowserInfo getBrowser() {
    return browser;
  }

  public void setBrowser(final BrowserInfo browser) {
    this.browser = browser;
  }

  public GeoInfo getGeo() {
    return geo;
  }

  public void setGeo(final GeoInfo geo) {
    this.geo = geo;
  }

  @Override
  public String toString() {
    return String.format("%s[persistentId=%s, sessionId=%s, browser=[%s], geo=[%s]]",
        getClass().getSimpleName(), persistentId, sessionId, browser, geo);
  }

  public static class BrowserInfo {
    private String name;
    private String type;
    private String os;
    private String language;

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public String getType() {
      return type;
    }

    public void setType(final String type) {
      this.type = type;
    }

    public String getOs() {
      return os;
    }

    public void setOs(final String os) {
      this.os = os;
    }

    public String getLanguage() {
      return language;
    }

    public void setLanguage(final String language) {
      this.language = language;
    }

    @Override
    public String toString() {
      return String.format("%s[name=%s, type=%s, os=%s, lang=%s]", getClass().getSimpleName(),
          name, type, os, language);
    }
  }

  public static class GeoInfo {
    private String city;
    private String country;
    private String representedCountry;
    private List<String> subdivisions;
    private String postal;

    public String getCity() {
      return city;
    }

    public void setCity(final String city) {
      this.city = city;
    }

    public String getCountry() {
      return country;
    }

    public void setCountry(final String country) {
      this.country = country;
    }

    public String getRepresentedCountry() {
      return representedCountry;
    }

    public void setRepresentedCountry(final String representedCountry) {
      this.representedCountry = representedCountry;
    }

    public List<String> getSubdivisions() {
      return subdivisions;
    }

    public void setSubdivisions(final List<String> subdivisions) {
      this.subdivisions = subdivisions;
    }

    public String getPostal() {
      return postal;
    }

    public void setPostal(final String postal) {
      this.postal = postal;
    }

    @Override
    public String toString() {
      return String.format("%s[city=%s, subdivisions=[%s], country=%s, repCountry=%s, postal=%s]",
          getClass().getSimpleName(), city, subdivisions, country, representedCountry, postal);
    }

  }
}
