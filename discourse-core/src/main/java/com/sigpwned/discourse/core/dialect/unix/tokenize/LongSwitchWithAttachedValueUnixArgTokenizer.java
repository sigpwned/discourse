package com.sigpwned.discourse.core.dialect.unix.tokenize;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.dialect.ArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;

public class LongSwitchWithAttachedValueUnixArgTokenizer implements ArgTokenizer, UnixDialectElement {
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

    int sep = arg.indexOf(LONG_NAME_VALUE_SEPARATOR, LONG_NAME_PREFIX.length());
    if (sep == -1)
      return Optional.empty();

    SwitchName name = SwitchName.fromString(arg.substring(LONG_NAME_PREFIX.length(), sep));
    String value = arg.substring(sep + 1, arg.length());

    return Optional.of(List.of(new SwitchNameToken(name, false), new ValueToken(value, true)));
  }
}
