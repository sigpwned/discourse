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
package com.sigpwned.discourse.core.dialect;

import java.util.List;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.args.token.ValueToken;

public class WindowsDialect implements Dialect {
  public static final WindowsDialect INSTANCE = new WindowsDialect();

  public static final String SWITCH_NAME_PREFIX = "/";

  @Override
  public DialectTokenizer newTokenizer() {
    return new DialectTokenizer() {
      @Override
      public List<Token> tokenize(String token) {
        if (token.startsWith(SWITCH_NAME_PREFIX)) {
          String text = token.substring(SWITCH_NAME_PREFIX.length(), token.length());
          return List.of(new SwitchNameToken(SwitchName.fromString(text), false));
        } else {
          return List.of(new ValueToken(token, false));
        }
      }
    };
  }

  @Override
  public String formatSwitchName(SwitchName name) {
    return SWITCH_NAME_PREFIX + name;
  }
}
