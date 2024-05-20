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

import static java.util.Collections.unmodifiableList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.Objects;

/**
 * A "bundle" token is a sequence of short switch names strung together, e.g., {@code -abc} which
 * represents the switches {@code -a}, {@code -b}, and {@code -c}.
 *
 * @see ShortSwitchNameArgumentCoordinate
 */
public final class BundleArgumentToken extends ArgumentToken {
  private final List<String> shortNames;

  public BundleArgumentToken(String text, List<String> shortNames) {
    super(text);
    if (shortNames == null) {
      throw new NullPointerException();
    }
    if (shortNames.isEmpty()) {
      throw new IllegalArgumentException("empty bundle");
    }
    if (!shortNames.stream().allMatch(ShortNameArgumentToken.PATTERN.asMatchPredicate())) {
      throw new IllegalArgumentException("invalid short names: " + shortNames.stream()
          .filter(not(ShortNameArgumentToken.PATTERN.asMatchPredicate())).collect(joining(", ")));
    }
    if (!text.equals(ShortNameArgumentToken.PREFIX + String.join("", shortNames))) {
      throw new IllegalArgumentException("invalid text: " + text);
    }
    this.shortNames = unmodifiableList(shortNames);
  }

  /**
   * @return the shortNames
   */
  public List<String> getShortNames() {
    return shortNames;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(shortNames);
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
    BundleArgumentToken other = (BundleArgumentToken) obj;
    return Objects.equals(shortNames, other.shortNames);
  }
}
