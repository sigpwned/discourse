package com.sigpwned.discourse.core.dialect;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.args.Token;

public class ArgTokenizerChain extends Chain<ArgTokenizer> implements ArgTokenizer {

  @Override
  public Optional<List<Token>> tokenize(String text) {
    for (ArgTokenizer tokenizer : this) {
      Optional<List<Token>> tokens = tokenizer.tokenize(text);
      if (tokens.isPresent())
        return tokens;
    }
    return Optional.empty();
  }
}
