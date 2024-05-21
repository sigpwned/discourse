package com.sigpwned.discourse.core.invocation.phase.parse.preprocess;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.args.Token;

public class TokenStreamPreprocessorChain extends Chain<TokenStreamPreprocessor>
    implements TokenStreamPreprocessor {

  @Override
  public List<Token> preprocessTokens(List<Token> tokens, InvocationContext context) {
    for (TokenStreamPreprocessor preprocessor : this) {
      tokens = preprocessor.preprocessTokens(new ArrayList<>(tokens), context);
    }
    return tokens;
  }
}
