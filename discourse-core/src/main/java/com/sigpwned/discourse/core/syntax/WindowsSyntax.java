package com.sigpwned.discourse.core.syntax;

import java.util.List;
import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.args.token.ValueToken;

public class WindowsSyntax implements Syntax {
  public static final WindowsSyntax INSTANCE = new WindowsSyntax();

  public static final String SWITCH_NAME_PREFIX = "/";

  @Override
  public SyntaxTokenizer newTokenizer() {
    return new SyntaxTokenizer() {
      @Override
      public List<Token> tokenize(String token) {
        if (token.startsWith(SWITCH_NAME_PREFIX)) {
          String text = token.substring(SWITCH_NAME_PREFIX.length(), token.length());
          return List.of(new SwitchNameToken(SwitchName.fromString(text)));
        } else {
          return List.of(new ValueToken(token, false));
        }
      }
    };
  }

  @Override
  public String formatSwitchName(SwitchName name) {
    return SWITCH_NAME_PREFIX + name;
  }
}
