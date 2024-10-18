package com.sigpwned.discourse.core.dialect.unix.tokenize;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PrimitiveIterator;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.dialect.ArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;

public class ShortSwitchBundleUnixArgTokenizer implements ArgTokenizer, UnixDialectElement {

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
    if (!arg.startsWith(SHORT_NAME_PREFIX) || arg.length() <= 2)
      return Optional.empty();

    String text = arg.substring(SHORT_NAME_PREFIX.length(), arg.length());

    List<Token> tokens = new ArrayList<>();
    PrimitiveIterator.OfInt iterator = text.chars().iterator();
    do {
      char ch = (char) iterator.nextInt();
      SwitchName name = SwitchName.fromString(String.valueOf(ch));
      tokens.add(new SwitchNameToken(name, iterator.hasNext()));
    } while (iterator.hasNext());

    return Optional.of(unmodifiableList(tokens));
  }
}
