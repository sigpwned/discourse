package com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.tokens;

import java.util.List;
import com.sigpwned.discourse.core.args.Token;

public interface TokensPreprocessor {
  public List<Token> preprocessTokens(List<Token> tokens);
}
