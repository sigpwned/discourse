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

import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.dialect.windows.format.SwitchNameTokenFormatter;
import com.sigpwned.discourse.core.dialect.windows.format.ValueTokenFormatter;
import com.sigpwned.discourse.core.dialect.windows.tokenize.SwitchNameWindowsArgTokenizer;
import com.sigpwned.discourse.core.dialect.windows.tokenize.ValueWindowsArgTokenizer;

public final class WindowsDialect implements Dialect {
  public static final WindowsDialect INSTANCE = new WindowsDialect();

  @Override
  public ArgTokenizer newTokenizer() {
    ArgTokenizerChain result = new ArgTokenizerChain();
    result.addLast(new SwitchNameWindowsArgTokenizer());
    result.addLast(new ValueWindowsArgTokenizer());
    return result;
  }

  @Override
  public TokenFormatter newTokenFormatter() {
    TokenFormatterChain result = new TokenFormatterChain();
    result.addLast(new SwitchNameTokenFormatter());
    result.addLast(new ValueTokenFormatter());
    return result;
  }
}
