package com.sigpwned.discourse.core.pipeline.invocation.command;

import com.sigpwned.discourse.core.pipeline.invocation.args.ArgsInvocationContext;

public interface CommandInvocationContext extends ArgsInvocationContext {
  public static final Key<CommandInvocationPipelineListener> LISTENER =
      Key.of(CommandInvocationPipelineListener.class);


  default CommandInvocationPipelineListener getPipelineListener() {
    return get(LISTENER).orElseThrow(() -> new IllegalStateException("no listener"));
  }
}
