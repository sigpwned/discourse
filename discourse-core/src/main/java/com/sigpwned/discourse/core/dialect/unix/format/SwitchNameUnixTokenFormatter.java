package com.sigpwned.discourse.core.dialect.unix.format;

import java.util.Optional;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.dialect.TokenFormatter;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;

public class SwitchNameUnixTokenFormatter implements TokenFormatter, UnixDialectElement {
  @Override
  public Optional<String> formatToken(Token token) {
    if (!(token instanceof SwitchNameToken switchNameToken))
      return Optional.empty();

    SwitchName name = switchNameToken.getName();

    if (name.length() == 1)
      return Optional.of(SHORT_NAME_PREFIX + name);
    else
      return Optional.of(LONG_NAME_PREFIX + name);
  }
}
