package com.sigpwned.discourse.core.pipeline.invocation.step.resolve.exception;

import static java.lang.String.format;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.ResolveException;

@SuppressWarnings("serial")
public class FailedCommandResolutionResolveException extends ResolveException {
  public FailedCommandResolutionResolveException() {
    // TODO We need a better way to resolve the supercommand class name..
    super(format("Failed to resolve command"));
  }
}
