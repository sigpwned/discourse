package com.sigpwned.discourse.core.dialect.windows.format;

import java.util.Optional;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.dialect.TokenFormatter;
import com.sigpwned.discourse.core.dialect.windows.WindowsDialectElement;

public class ValueTokenFormatter implements TokenFormatter, WindowsDialectElement {

  @Override
  public Optional<String> formatToken(Token token) {
    if (!(token instanceof ValueToken valueToken))
      return Optional.empty();
    return Optional.of(valueToken.getValue());
  }
}
