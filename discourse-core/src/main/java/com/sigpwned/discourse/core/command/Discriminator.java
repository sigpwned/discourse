/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
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
package com.sigpwned.discourse.core.command;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Discriminator {
  public static Discriminator fromString(String text) {
    return of(text);
  }

  public static Discriminator of(String text) {
    return new Discriminator(text);
  }

  public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9](?:[-._]?[a-zA-Z0-9])*");

  private final String text;

  public Discriminator(String text) {
    this.text = requireNonNull(text);
    if (!PATTERN.matcher(text).matches())
      throw new IllegalArgumentException("Invalid discriminator: " + text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Discriminator other = (Discriminator) obj;
    return Objects.equals(text, other.text);
  }

  @Override
  public String toString() {
    return text;
  }
}
