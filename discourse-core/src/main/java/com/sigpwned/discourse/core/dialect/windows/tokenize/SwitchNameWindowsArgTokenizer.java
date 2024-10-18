package com.sigpwned.discourse.core.dialect.windows.tokenize;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.dialect.ArgTokenizer;
import com.sigpwned.discourse.core.dialect.windows.WindowsDialectElement;

public class SwitchNameWindowsArgTokenizer implements ArgTokenizer, WindowsDialectElement {
  public static final String SWITCH_NAME_PREFIX = "/";

  @Override
  public Optional<List<Token>> tokenize(String arg) {
    if (!arg.startsWith(SWITCH_NAME_PREFIX))
      return Optional.empty();

    String text = arg.substring(SWITCH_NAME_PREFIX.length(), arg.length());

    if (text.length() == 0)
      return Optional.of(List.of(new ValueToken(SWITCH_NAME_PREFIX, false)));

    return Optional.of(List.of(new SwitchNameToken(SwitchName.fromString(text), false)));
  }
}
