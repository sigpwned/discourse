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
package com.sigpwned.discourse.core.args;

import java.util.Objects;
import com.sigpwned.discourse.core.dialect.UnixDialect;

public abstract class Token {
  /**
   * Whether or not this token is syntactically attached to the previous token. For example, in
   * {@link UnixDialect the unix dialect}, constructs like {@code -abc} parse as three separate
   * tokens, {@code -a} and {@code -b} and {@code -c}, with {@code -b} and {@code -c} being attached
   * to the previous token. This can be important for determining the meaning of the token.
   */
  private final boolean attached;

  public Token(boolean attached) {
    this.attached = attached;
  }

  public boolean isAttached() {
    return attached;
  }

  @Override
  public int hashCode() {
    return Objects.hash(attached);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Token other = (Token) obj;
    return attached == other.attached;
  }

  @Override
  public String toString() {
    return "Token [attached=" + attached + "]";
  }
}
