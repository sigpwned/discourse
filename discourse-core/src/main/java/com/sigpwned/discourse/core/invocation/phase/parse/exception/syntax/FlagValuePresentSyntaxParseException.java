package com.sigpwned.discourse.core.invocation.phase.parse.exception.syntax;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.invocation.phase.parse.exception.SyntaxParseException;

/**
 * <p>
 * Thrown when a flag switch is given a value, e.g., <code>--flag=value</code>. Flag switches never
 * take a value, by definition.
 * </p>
 */
public class FlagValuePresentSyntaxParseException extends SyntaxParseException {
  private static final long serialVersionUID = -5704560154794184050L;

  private final String flagSwitchName;

  public FlagValuePresentSyntaxParseException(String flagSwitchName) {
    super("flag %s takes no value, but one was given".formatted(flagSwitchName));
    this.flagSwitchName = requireNonNull(flagSwitchName);
  }

  public String getFlagSwitchName() {
    return flagSwitchName;
  }
}
