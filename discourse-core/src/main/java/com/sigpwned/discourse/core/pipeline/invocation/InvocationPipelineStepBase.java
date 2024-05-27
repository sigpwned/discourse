package com.sigpwned.discourse.core.pipeline.invocation;

public abstract class InvocationPipelineStepBase {
  public static final InvocationContext.Key<InvocationPipelineListener> INVOCATION_PIPELINE_LISTENER_KEY =
      InvocationContext.Key.of(InvocationPipelineListener.class);

  protected InvocationPipelineListener getListener(InvocationContext context) {
    return context.get(INVOCATION_PIPELINE_LISTENER_KEY).orElseThrow(() -> {
      // TODO better exception
      return new IllegalStateException("No listener");
    });
  }
}
