package com.sigpwned.discourse.core.pipeline.invocation.command.step;

import java.util.Map;
import java.util.function.BiFunction;
import com.sigpwned.discourse.core.pipeline.invocation.command.CommandInvocationContext;

public class FinishStep {
  public <T> T finish(BiFunction<Map<String, Object>, CommandInvocationContext, T> finisher,
      Map<String, Object> reducedArgs, CommandInvocationContext context) {
    return finisher.apply(reducedArgs, context);
  }
}
