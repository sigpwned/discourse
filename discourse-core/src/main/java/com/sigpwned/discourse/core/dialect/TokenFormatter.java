package com.sigpwned.discourse.core.dialect;

import java.util.Optional;
import com.sigpwned.discourse.core.args.Token;

public interface TokenFormatter {
  public Optional<String> formatToken(Token token);
}
