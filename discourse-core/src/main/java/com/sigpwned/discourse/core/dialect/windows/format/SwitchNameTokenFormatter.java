package com.sigpwned.discourse.core.dialect.windows.format;

import java.util.Optional;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.dialect.TokenFormatter;
import com.sigpwned.discourse.core.dialect.windows.WindowsDialectElement;

public class SwitchNameTokenFormatter implements TokenFormatter, WindowsDialectElement {

  @Override
  public Optional<String> formatToken(Token token) {
    if (!(token instanceof SwitchNameToken switchNameToken))
      return Optional.empty();

    SwitchName name = switchNameToken.getName();

    return Optional.of(SWITCH_NAME_PREFIX + name);
  }
}
