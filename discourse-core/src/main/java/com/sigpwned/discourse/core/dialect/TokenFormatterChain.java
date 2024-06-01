package com.sigpwned.discourse.core.dialect;

import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.args.Token;

public class TokenFormatterChain extends Chain<TokenFormatter> implements TokenFormatter {
  @Override
  public Optional<String> formatToken(Token token) {
    for (TokenFormatter formatter : this) {
      Optional<String> result = formatter.formatToken(token);
      if (result.isPresent())
        return result;
    }
    return Optional.empty();
  }
}
