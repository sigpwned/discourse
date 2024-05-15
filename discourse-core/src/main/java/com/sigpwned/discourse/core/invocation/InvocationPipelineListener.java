package com.sigpwned.discourse.core.invocation;

import com.sigpwned.discourse.core.invocation.phase.eval.impl.DefaultEvalPhaseListener;
import com.sigpwned.discourse.core.invocation.phase.factory.impl.DefaultFactoryPhaseListener;
import com.sigpwned.discourse.core.invocation.phase.parse.impl.DefaultParsePhaseListener;
import com.sigpwned.discourse.core.invocation.phase.resolve.impl.DefaultResolvePhaseListener;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.DefaultScanPhaseListener;

public interface InvocationPipelineListener extends DefaultScanPhaseListener,
    DefaultResolvePhaseListener, DefaultParsePhaseListener, DefaultEvalPhaseListener,
    DefaultFactoryPhaseListener {

  default void beforeInvocation() {
  }

  default <T> void afterInvocation(Invocation<T> invocation) {

  }

  default void catchInvocation(Throwable t) {

  }

  default void finallyInvocation() {

  }
}
