package com.sigpwned.discourse.core.module.core;

import com.sigpwned.discourse.core.format.ExceptionFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;

public class ExceptionHandlingInvocationPipelineListener implements InvocationPipelineListener {
  @Override
  public void catchPipeline(Throwable t, InvocationContext context) {
    ExceptionFormatter formatter = null;
    formatter.formatException(null, t, null);
  }
}
