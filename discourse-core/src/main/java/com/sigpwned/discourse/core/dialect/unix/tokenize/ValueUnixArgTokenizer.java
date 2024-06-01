package com.sigpwned.discourse.core.dialect.unix.tokenize;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.dialect.ArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;

public class ValueUnixArgTokenizer implements ArgTokenizer, UnixDialectElement {
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
