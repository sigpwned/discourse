package com.sigpwned.discourse.core.pipeline.invocation.step.plan;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.command.tree.Command;
import com.sigpwned.discourse.core.exception.DiscourseException;

@SuppressWarnings("serial")
public abstract class PlanException extends DiscourseException {
  private final Command<?> command;

  protected PlanException(Command<?> command, String message) {
    super(message);
    this.command = requireNonNull(command);
  }

  protected PlanException(Command<?> command, String message, Throwable cause) {
    super(message, cause);
    this.command = requireNonNull(command);
  }

  public Command<?> getCommand() {
    return this.command;
  }
}
