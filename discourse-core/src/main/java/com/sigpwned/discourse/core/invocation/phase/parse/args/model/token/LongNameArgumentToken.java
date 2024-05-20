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

import static java.lang.String.format;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A "long name" argument token, e.g., {@code --foo}
 *
 * @see LongSwitchNameArgumentCoordinate
 */
public final class LongNameArgumentToken extends ArgumentToken {
  /* default */ static final String PREFIX = "--";

  /* default */ static final Pattern PATTERN = Pattern.compile(format("%s(?:[-._]?[a-zA-Z0-9])+"));

  private final String longName;

  public LongNameArgumentToken(String text, String longName) {
    super(text);
    if (longName == null) {
      throw new NullPointerException();
    }
    if (!PATTERN.matcher(longName).matches()) {
      throw new IllegalArgumentException("invalid long name: " + longName);
    }
    if (!text.equals(PREFIX + longName)) {
      throw new IllegalArgumentException(
          "text does not match long name: " + text + " != " + PREFIX + longName);
    }
    this.longName = longName;
  }

  /**
   * @return the longName
   */
  public String getLongName() {
    return longName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(longName);
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
    LongNameArgumentToken other = (LongNameArgumentToken) obj;
    return Objects.equals(longName, other.longName);
  }
}
