package com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception;

import static java.lang.String.format;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.PlanException;

@SuppressWarnings("serial")
public class NonLeafCommandPlanException extends PlanException {
  public NonLeafCommandPlanException(Command<?> command) {
    // TODO Some kind of command name would be really good here...
    super(command, format("Command %s is not a leaf command", command));
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    // TODO We need a better way to get the name of the command class...
    return new Object[] {getCommand().getClass().getName()};
  }
}
