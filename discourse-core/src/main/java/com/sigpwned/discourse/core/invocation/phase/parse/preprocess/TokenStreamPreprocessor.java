package com.sigpwned.discourse.core.invocation.phase.parse.preprocess;

import java.util.List;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.args.Token;

public interface TokenStreamPreprocessor {
  public List<Token> preprocessTokens(List<Token> tokens, InvocationContext context);
}
