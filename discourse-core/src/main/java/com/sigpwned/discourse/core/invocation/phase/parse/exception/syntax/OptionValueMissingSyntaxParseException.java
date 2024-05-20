package com.sigpwned.discourse.core.invocation.phase.parse.exception.syntax;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.invocation.phase.parse.exception.SyntaxParseException;

/**
 * Thrown when an option requires a value, but none was given. For example, this exception would be
 * thrown if an option {@code -b} were specified in the middle of a bundle as in {@code -abc}.
 * Options always require values, by definition.
 */
public class OptionValueMissingSyntaxParseException extends SyntaxParseException {
  private static final long serialVersionUID = -546183389765696066L;

  private final String optionSwitchName;

  public OptionValueMissingSyntaxParseException(String optionSwitchName) {
    super("option %s requires value, but none was given".formatted(optionSwitchName));
    this.optionSwitchName = requireNonNull(optionSwitchName);
  }

  public String getOptionSwitchName() {
    return optionSwitchName;
  }
}
