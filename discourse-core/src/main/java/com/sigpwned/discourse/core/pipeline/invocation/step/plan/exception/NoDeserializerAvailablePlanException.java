package com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.PlanException;

@SuppressWarnings("serial")
public class NoDeserializerAvailablePlanException extends PlanException {
  private final LeafCommand<?> command;
  private final LeafCommandProperty property;

  public NoDeserializerAvailablePlanException(LeafCommand<?> command,
      LeafCommandProperty property) {
    super(format("No deserializer available for command %s property %s", command,
        property.getName()));
    this.command = requireNonNull(command);
    this.property = requireNonNull(property);
  }

  public LeafCommand<?> getCommand() {
    return command;
  }

  public LeafCommandProperty getProperty() {
    return property;
  }
}
