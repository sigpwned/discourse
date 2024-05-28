package com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.PlanException;

@SuppressWarnings("serial")
public class NoDeserializerAvailablePlanException extends PlanException {
  private final LeafCommandProperty property;

  public NoDeserializerAvailablePlanException(LeafCommand<?> command,
      LeafCommandProperty property) {
    super(command, format("No deserializer available for command %s property %s", command,
        property.getName()));
    this.property = requireNonNull(property);
  }

  public LeafCommand<?> getCommand() {
    return (LeafCommand<?>) super.getCommand();
  }

  public LeafCommandProperty getProperty() {
    return property;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    // TODO We need a better way to get the name of the command class...
    return new Object[] {getCommand().getClass().getName(), getProperty().getName()};
  }
}
