package com.sigpwned.discourse.core.format.help;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class TextFormatterChain extends Chain<TextFormatter> implements TextFormatter {
  @Override
  public String formatText(String text, InvocationContext context) {
    for (TextFormatter formatter : this) {
      text = formatter.formatText(text, context);
    }
    return text;
  }
}
