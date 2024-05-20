package com.sigpwned.discourse.core.invocation.phase.parse.exception.syntax;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.invocation.phase.parse.exception.SyntaxParseException;

/**
 * Thrown when a switch is not recognized. For example, this exception would be thrown if an
 * application expects switches {@code -a} and {@code -b}, but the user provides a switch
 * {@code -c}.
 */
public class NoSuchSwitchSyntaxParseException extends SyntaxParseException {
  private static final long serialVersionUID = -5704560154794184050L;

  private final String unrecognizedSwitchName;

  public NoSuchSwitchSyntaxParseException(String unrecognizedSwitchName) {
    super("unrecognized switch %s".formatted(unrecognizedSwitchName));
    this.unrecognizedSwitchName = requireNonNull(unrecognizedSwitchName);
  }

  public String getFlagSwitchName() {
    return unrecognizedSwitchName;
  }
}
