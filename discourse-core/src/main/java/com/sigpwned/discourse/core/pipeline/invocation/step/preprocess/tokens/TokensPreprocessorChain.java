package com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.tokens;

import java.util.List;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.args.Token;

public class TokensPreprocessorChain extends Chain<TokensPreprocessor>
    implements TokensPreprocessor {

  @Override
  public List<Token> preprocessTokens(List<Token> tokens) {
    for (TokensPreprocessor preprocessor : this) {
      tokens = preprocessor.preprocessTokens(tokens);
    }
    return tokens;
  }
}
