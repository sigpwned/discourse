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
import com.sigpwned.discourse.core.dialect.unix.format.SwitchNameUnixTokenFormatter;
import com.sigpwned.discourse.core.dialect.unix.format.ValueUnixTokenFormatter;
import com.sigpwned.discourse.core.dialect.unix.tokenize.LongSwitchArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.tokenize.LongSwitchWithAttachedValueArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.tokenize.SeparatorUnixArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.tokenize.ShortSwitchBundleUnixArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.tokenize.ShortSwitchPrefixUnixArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.tokenize.ShortSwitchUnixArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.tokenize.ValueUnixArgTokenizer;

public final class UnixDialect implements Dialect {
  public static final UnixDialect INSTANCE = new UnixDialect();

  @Override
  public ArgTokenizer newTokenizer() {
    ArgTokenizerChain result = new ArgTokenizerChain();
    result.addLast(new SeparatorUnixArgTokenizer());
    result.addLast(new ShortSwitchPrefixUnixArgTokenizer());
    result.addLast(new LongSwitchWithAttachedValueArgTokenizer());
    result.addLast(new LongSwitchArgTokenizer());
    result.addLast(new ShortSwitchUnixArgTokenizer());
    result.addLast(new ShortSwitchBundleUnixArgTokenizer());
    result.addLast(new ValueUnixArgTokenizer());
    return result;
  }

  @Override
  public TokenFormatter newTokenFormatter() {
    TokenFormatterChain result = new TokenFormatterChain();
    result.addLast(new SwitchNameUnixTokenFormatter());
    result.addLast(new ValueUnixTokenFormatter());
    return result;
  }
}
