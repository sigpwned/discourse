/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core.invocation.phase.parse.args.model.token;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A "short name" argument token, e.g., {@code -f}
 *
 * @see ShortSwitchNameArgumentCoordinate
 */
public final class ShortNameArgumentToken extends ArgumentToken {
  /* default */ static final String PREFIX = "-";

  /* default */ static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9]");

  private final String shortName;

  public ShortNameArgumentToken(String text, String shortName) {
    super(text);
    if (shortName == null) {
      throw new NullPointerException();
    }
    if (!PATTERN.matcher(shortName).matches()) {
      throw new IllegalArgumentException("invalid short name: " + shortName);
    }
    if (!text.equals(PREFIX + shortName)) {
      throw new IllegalArgumentException(
          "text does not match short name: " + text + " != " + PREFIX + shortName);
    }
    this.shortName = shortName;
  }

  /**
   * @return the shortName
   */
  public String getShortName() {
    return shortName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(shortName);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ShortNameArgumentToken other = (ShortNameArgumentToken) obj;
    return Objects.equals(shortName, other.shortName);
  }
}
