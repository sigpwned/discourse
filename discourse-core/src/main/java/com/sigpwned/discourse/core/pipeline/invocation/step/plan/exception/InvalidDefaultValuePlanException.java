package com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.command.tree.Command;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.PlanException;

/**
 * Used when a command property has a default value that cannot be parsed by its associated
 * deserializer.
 */
@SuppressWarnings("serial")
public class InvalidDefaultValuePlanException extends PlanException {
  private final String propertyName;
  private final String defaultValue;

  // TODO It would be nice to have some kind of command name...
  public InvalidDefaultValuePlanException(Command<?> command, String propertyName,
      String defaultValue, Throwable cause) {
    super(command, format("Command %s property %s has invalid default value %s",
        command.getClass().getName(), propertyName, defaultValue), cause);
    this.propertyName = requireNonNull(propertyName);
    this.defaultValue = requireNonNull(defaultValue);
  }

  public String getPropertyName() {
    return propertyName;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getCommand().getClass().getName(), getPropertyName(), getDefaultValue()};
  }
}
