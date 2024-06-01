package com.sigpwned.discourse.core.dialect.unix.tokenize;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.dialect.ArgTokenizer;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;

public class SeparatorUnixArgTokenizer implements ArgTokenizer, UnixDialectElement {
  private boolean separatorFound = false;

  /**
   * <p>
   * Before a special {@link #SEPARATOR separator} token, this tokenizer will never return a token.
   * When the separator token is encountered, the tokenizer will return a list containing zero
   * tokens. All tokens after the separator token will be returned as value tokens, even if they
   * start with the {@link ShortSwitchUnixArgTokenizer#SHORT_NAME_PREFIX short switch prefix} or
   * {@link LongSwitchArgTokenizer#LONG_NAME_PREFIX long switch prefix}.
   * </p>
   * 
   * @param arg The token to tokenize.
   */
  @Override
  public Optional<List<Token>> tokenize(String arg) {
    if (isSeparatorFound())
      return Optional.of(List.of(new ValueToken(arg, false)));

    if (arg.equals(SEPARATOR)) {
      separatorFound = true;
      return Optional.of(List.of());
    }

    return Optional.empty();
  }

  public boolean isSeparatorFound() {
    return separatorFound;
  }
}
