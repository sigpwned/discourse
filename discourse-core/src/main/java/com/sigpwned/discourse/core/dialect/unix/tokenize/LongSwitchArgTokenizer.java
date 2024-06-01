package com.sigpwned.discourse.core.dialect.unix.tokenize;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.dialect.ArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;

public class LongSwitchArgTokenizer implements ArgTokenizer, UnixDialectElement {
  /**
   * <p>
   * Handles tokens that start with a {@link #LONG_NAME_PREFIX long name prefix}, namely:
   * </p>
   * 
   * <ul>
   * <li>The literal value {@code "--"}</li>
   * <li>A long switch, e.g., {@code "--hello"}</li>
   * <li>A long switch with attached value, e.g., {@code "--hello=world"}</li>
   * </ul>
   * 
   * @param arg The token to tokenize.
   */
  @Override
  public Optional<List<Token>> tokenize(String arg) {
    if (!arg.startsWith(LONG_NAME_PREFIX))
      return Optional.empty();

    String text = arg.substring(LONG_NAME_PREFIX.length(), arg.length());

    SwitchName name = SwitchName.fromString(text);

    return Optional.of(List.of(new SwitchNameToken(name, false)));
  }
}
