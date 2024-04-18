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
package com.sigpwned.discourse.core;

import static java.lang.String.*;

import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.util.Generated;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A discriminator is a string that is used to indicate which
 * {@link MultiCommand#getSubcommands() subcommand} of a {@link MultiCommand} should be executed. It
 * must match the regular expression {@code "[a-zA-Z0-9][-a-zA-Z0-9_]*"}.
 */
public class Discriminator implements Comparable<Discriminator> {

  public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9][-a-zA-Z0-9_]*");

  public static Discriminator fromString(String s) {
    return new Discriminator(s);
  }

  private final String text;

  public Discriminator(String text) {
    if (!PATTERN.matcher(text).matches()) {
      throw new IllegalArgumentException(format("invalid discriminator '%s'", text));
    }
    this.text = text;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Discriminator other = (Discriminator) obj;
    return Objects.equals(text, other.text);
  }

  @Override
  public String toString() {
    return getText();
  }

  public static final Comparator<Discriminator> COMPARATOR = Comparator.comparing(
      Discriminator::toString);

  @Override
  public int compareTo(Discriminator that) {
    return COMPARATOR.compare(this, that);
  }
}
