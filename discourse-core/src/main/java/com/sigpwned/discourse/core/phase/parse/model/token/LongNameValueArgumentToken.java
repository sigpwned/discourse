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
package com.sigpwned.discourse.core.phase.parse.model.token;

import com.sigpwned.discourse.core.model.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.util.Generated;
import java.util.Objects;

/**
 * A "long name value" token that represents the single-token combination of a long name and a
 * value, e.g., {@code --foo=bar}. Note that the value may be empty, (as in {@code --foo=}). Note
 * that the switch and value may be separate as well, which would be represented by two separate
 * tokens.
 *
 * @see LongSwitchNameCoordinate
 * @see LongNameArgumentToken
 * @see ValueArgumentToken
 */
public final class LongNameValueArgumentToken extends ArgumentToken {

  private final String longName;
  private final String value;

  public LongNameValueArgumentToken(String text, String longName, String value) {
    super(text);
    if (longName == null) {
      throw new NullPointerException();
    }
    if (!LongSwitchNameCoordinate.PATTERN.matcher(longName).matches()) {
      throw new IllegalArgumentException("invalid long name: " + longName);
    }
    if (value == null) {
      throw new NullPointerException();
    }
    this.longName = longName;
    this.value = value;
  }

  /**
   * @return the shortName
   */
  public String getLongName() {
    return longName;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(longName, value);
    return result;
  }

  @Override
  @Generated
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
    LongNameValueArgumentToken other = (LongNameValueArgumentToken) obj;
    return Objects.equals(longName, other.longName) && Objects.equals(value, other.value);
  }
}
