package com.sigpwned.discourse.core.dialect.unix.format;

import java.util.Optional;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.dialect.TokenFormatter;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;

public class ValueUnixTokenFormatter implements TokenFormatter, UnixDialectElement {
  @Override
  public Optional<String> formatToken(Token token) {
    if (!(token instanceof ValueToken valueToken))
      return Optional.empty();

    return Optional.of(valueToken.getValue());
  }
}
