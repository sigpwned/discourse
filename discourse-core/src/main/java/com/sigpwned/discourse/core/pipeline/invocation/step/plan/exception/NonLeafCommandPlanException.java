package com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.PlanException;

@SuppressWarnings("serial")
public class NonLeafCommandPlanException extends PlanException {
  private final Command<?> command;

  public NonLeafCommandPlanException(Command<?> command) {
    // TODO Some kind of command name would be really good here...
    super(format("Command %s is not a leaf command", command));
    this.command = requireNonNull(command);
  }

  public Command<?> getCommand() {
    return command;
  }
}
