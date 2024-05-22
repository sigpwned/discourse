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
package com.sigpwned.discourse.core.syntax;

import static java.util.Objects.requireNonNull;
import java.util.List;
import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.ValueToken;

public class SeparableSyntax implements Syntax {
  public static final String DEFAULT_SEPARATOR = "--";

  private final Syntax delegate;
  private final String separator;

  public SeparableSyntax(Syntax delegate) {
    this(delegate, DEFAULT_SEPARATOR);
  }

  public SeparableSyntax(Syntax delegate, String separator) {
    this.delegate = requireNonNull(delegate);
    this.separator = requireNonNull(separator);
    if (separator.isEmpty()) {
      throw new IllegalArgumentException("separator must not be empty");
    }
  }

  @Override
  public SyntaxTokenizer newTokenizer() {
    SyntaxTokenizer subtokenizer = getDelegate().newTokenizer();
    return new SyntaxTokenizer() {
      private boolean separated = false;

      @Override
      public List<Token> tokenize(String text) {
        if (separated) {
          return List.of(new ValueToken(text, false));
        } else if (text.equals(getSeparator())) {
          separated = true;
          return List.of();
        } else {
          return subtokenizer.tokenize(text);
        }
      }
    };

  }

  @Override
  public String formatSwitchName(SwitchName name) {
    // TODO Auto-generated method stub
    return null;
  }

  private Syntax getDelegate() {
    return delegate;
  }

  private String getSeparator() {
    return separator;
  }
}
