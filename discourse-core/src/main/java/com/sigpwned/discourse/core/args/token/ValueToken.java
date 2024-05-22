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
package com.sigpwned.discourse.core.args.token;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import com.sigpwned.discourse.core.args.Token;

public class ValueToken extends Token {
  private final String value;

  /**
   * Whether the value is syntactically attached to the option or not, e.g., in {@code --foo=bar},
   * the value {@code bar} is attached to the option {@code --foo}.
   */
  private final boolean attached;

  public ValueToken(String value, boolean attached) {
    this.value = requireNonNull(value);
    this.attached = attached;
  }

  public String getValue() {
    return value;
  }

  public boolean isAttached() {
    return attached;
  }

  @Override
  public int hashCode() {
    return Objects.hash(attached, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ValueToken other = (ValueToken) obj;
    return attached == other.attached && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return "ValueToken[" + value + ", " + attached + "]";
  }
}
