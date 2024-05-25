package com.sigpwned.discourse.core.pipeline.invocation.args;

import com.sigpwned.discourse.core.Syntax;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface ArgsInvocationContext extends InvocationContext {
  public static final Key<Syntax> SYNTAX = Key.of(Syntax.class);

  public static final Key<ArgsInvocationPipelineListener> LISTENER =
      Key.of(ArgsInvocationPipelineListener.class);

  default Syntax getSyntax() {
    return get(SYNTAX).orElseThrow(() -> new IllegalStateException("no syntax"));
  }

  default ArgsInvocationPipelineListener getListener() {
    return get(ArgsInvocationPipelineListener.class)
        .orElseThrow(() -> new IllegalStateException("no listener"));
  }
}
