package com.sigpwned.discourse.core.pipeline.invocation;

import com.sigpwned.discourse.core.format.ExceptionFormatter;

public abstract class InvocationPipelineStepBase {
  public static final InvocationContext.Key<InvocationPipelineListener> INVOCATION_PIPELINE_LISTENER_KEY =
      InvocationContext.Key.of(InvocationPipelineListener.class);

  public static final InvocationContext.Key<ExceptionFormatter> EXCEPTION_FORMATTER_KEY =
      InvocationContext.Key.of(ExceptionFormatter.class);

  protected InvocationPipelineListener getListener(InvocationContext context) {
    return context.get(INVOCATION_PIPELINE_LISTENER_KEY).orElseThrow(() -> {
      // TODO better exception
      return new IllegalStateException("No listener");
    });
  }
}
