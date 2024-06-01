package com.sigpwned.discourse.core.dialect.windows.tokenize;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.dialect.ArgTokenizer;
import com.sigpwned.discourse.core.dialect.windows.WindowsDialectElement;

public class ValueWindowsArgTokenizer implements ArgTokenizer, WindowsDialectElement {
  /**
   * <p>
   * Returns the given argument as a single {@link ValueToken value token}.
   * </p>
   * 
   * @param arg The token to tokenize.
   */
  @Override
  public Optional<List<Token>> tokenize(String arg) {
    return Optional.of(List.of(new ValueToken(arg, false)));
  }
}
