package com.sigpwned.discourse.core.syntax;

import java.util.List;
import com.sigpwned.discourse.core.args.Token;

public interface SyntaxTokenizer {
  public List<Token> tokenize(String text);
}
