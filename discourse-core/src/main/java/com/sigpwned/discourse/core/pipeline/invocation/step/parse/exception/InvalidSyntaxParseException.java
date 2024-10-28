package com.sigpwned.discourse.core.pipeline.invocation.step.parse.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.pipeline.invocation.step.parse.ParseException;

@SuppressWarnings("serial")
public class InvalidSyntaxParseException extends ParseException {
  private final String text;

  public InvalidSyntaxParseException(String text) {
    super(format("Invalid syntax: %s", text));
    this.text = requireNonNull(text);
  }

  public String getText() {
    return text;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getText()};
  }
}
