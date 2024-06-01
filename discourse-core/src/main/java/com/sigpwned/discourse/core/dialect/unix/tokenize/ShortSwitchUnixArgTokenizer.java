package com.sigpwned.discourse.core.dialect.unix.tokenize;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.dialect.ArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;

public class ShortSwitchUnixArgTokenizer implements ArgTokenizer, UnixDialectElement {

  /**
   * <p>
   * Handles tokens that start with a {@link #SHORT_NAME_PREFIX short name prefix}, namely:
   * </p>
   * 
   * <ul>
   * <li>The literal value {@code "-"}</li>
   * <li>A single short switch, e.g., {@code "-x"}</li>
   * <li>A bundle of short switches, e.g., {@code "-xyz"}</li>
   * </ul>
   * 
   * @param arg The token to tokenize.
   */
  @Override
  public Optional<List<Token>> tokenize(String arg) {
    if (!arg.startsWith(SHORT_NAME_PREFIX) || arg.length() != 2)
      return Optional.empty();

    String text = arg.substring(SHORT_NAME_PREFIX.length(), arg.length());

    return Optional.of(List.of(new SwitchNameToken(SwitchName.fromString(text), false)));
  }
}
