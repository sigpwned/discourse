package com.sigpwned.discourse.core.pipeline.invocation;

public abstract class InvocationPipelineStepBase implements InvocationPipelineStep {
  protected InvocationPipelineListener getListener(InvocationContext context) {
    return context.get(InvocationPipelineStep.INVOCATION_PIPELINE_LISTENER_KEY).orElseThrow();
  }
}
