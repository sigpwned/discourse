package com.sigpwned.discourse.core.syntax;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.args.token.ValueToken;

public class UnixSyntax implements Syntax {
  public static final UnixSyntax INSTANCE = new UnixSyntax();

  public static final String LONG_NAME_PREFIX = "--";

  public static final char LONG_NAME_VALUE_SEPARATOR = '=';

  public static final String SHORT_NAME_PREFIX = "-";

  @Override
  public SyntaxTokenizer newTokenizer() {
    return new SyntaxTokenizer() {
      @Override
      public List<Token> tokenize(String token) {
        if (token.startsWith(LONG_NAME_PREFIX)) {
          String text = token.substring(LONG_NAME_PREFIX.length(), token.length());

          int sep = text.indexOf(LONG_NAME_VALUE_SEPARATOR);
          if (sep != -1) {
            SwitchName switchName = SwitchName.fromString(text.substring(0, sep));
            String value = text.substring(sep + 1, text.length());
            return List.of(new SwitchNameToken(switchName), new ValueToken(value, true));
          } else {
            return List.of(new SwitchNameToken(SwitchName.fromString(text)));
          }
        } else if (token.startsWith(SHORT_NAME_PREFIX)) {
          String text = token.substring(SHORT_NAME_PREFIX.length(), token.length());
          if (text.length() == 0) {
            return List.of(new ValueToken(SHORT_NAME_PREFIX, false));
          } else if (text.length() == 1) {
            return List.of(new SwitchNameToken(SwitchName.fromString(text)));
          } else {
            List<Token> result = new ArrayList<>();
            for (int i = 0; i < text.length(); i++) {
              result.add(new SwitchNameToken(SwitchName.fromString(text.substring(i, i + 1))));
            }
            return unmodifiableList(result);
          }
        } else {
          return List.of(new ValueToken(token, false));
        }
      }
    };
  }

  @Override
  public String formatSwitchName(SwitchName name) {
    if (name.length() == 1) {
      return "-" + name.getText();
    } else {
      return "--" + name.getText();
    }
  }
}
