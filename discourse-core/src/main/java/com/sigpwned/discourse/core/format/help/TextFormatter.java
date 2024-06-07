package com.sigpwned.discourse.core.format.help;

import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface TextFormatter {
  public String formatText(String text, InvocationContext context);
}
