package com.sigpwned.discourse.core.pipeline.invocation.step.resolve.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.command.tree.SuperCommand;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.ResolveException;

@SuppressWarnings("serial")
public class PartialCommandResolutionResolveException extends ResolveException {
  private final SuperCommand<?> supercommand;

  public PartialCommandResolutionResolveException(SuperCommand<?> supercommand) {
    // TODO We need a better way to resolve the supercommand class name..
    super(format("Partial command resolution for supercommand %s",
        supercommand.getClass().getName()));
    this.supercommand = requireNonNull(supercommand);
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getSupercommand().getClass().getName()};
  }

  public SuperCommand<?> getSupercommand() {
    return supercommand;
  }
}
