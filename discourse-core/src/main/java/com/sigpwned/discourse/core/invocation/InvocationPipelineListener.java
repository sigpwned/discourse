package com.sigpwned.discourse.core.invocation;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.eval.EvalPhaseListener;
import com.sigpwned.discourse.core.invocation.phase.factory.FactoryPhaseListener;
import com.sigpwned.discourse.core.invocation.phase.parse.ParsePhaseListener;
import com.sigpwned.discourse.core.invocation.phase.resolve.ResolvePhaseListener;
import com.sigpwned.discourse.core.invocation.phase.scan.ScanPhaseListener;

public interface InvocationPipelineListener
    extends ScanPhaseListener, ResolvePhaseListener, ParsePhaseListener,
    EvalPhaseListener, FactoryPhaseListener {

  default <T> void beforeInvocation(Class<T> rootClazz, List<String> args) {}

  default <T> void afterInvocation(Class<T> rootClazz, List<String> args,
      Invocation<? extends T> invocation) {}

  default void catchInvocation(Throwable t) {}

  default void finallyInvocation() {}

  // SCAN PHASE ///////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanPhase(Class<T> clazz) {}

  default <T> void afterScanPhase(Class<T> clazz, RootCommand<T> command) {}

  default <T> void catchScanPhase(Throwable problem) {}

  default <T> void finallyScanPhase() {}

  // RESOLVE PHASE ////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeResolvePhase(RootCommand<T> command, List<String> originalArgs) {}

  default <T> void afterResolvePhase(RootCommand<T> command, List<String> originalArgs,
      Command<? extends T> resolvedCommand, List<CommandDereference<? extends T>> dereferences,
      List<String> remainingArgs) {}

  default <T> void catchResolvePhase(Throwable problem) {}

  default <T> void finallyResolvePhase() {}

  // PARSE PHASE //////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeParsePhase(Command<? extends T> resolvedCommand,
      List<String> remainingArgs) {}

  default <T> void afterParsePhase(Command<? extends T> resolvedCommand, List<String> remainingArgs,
      List<Map.Entry<String, String>> namedArgs) {}

  default <T> void catchParsePhase(Throwable problem) {}

  default <T> void finallyParsePhase() {}

  // EVAL PHASE ///////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeEvalPhase(Command<T> resolvedCommand,
      List<Map.Entry<String, String>> namedArgs) {}

  default <T> void afterEvalPhase(Command<T> resolvedCommand,
      List<Map.Entry<String, String>> namedArgs, Map<String, Object> state) {}

  default <T> void catchEvalPhase(Throwable problem) {}

  default <T> void finallyEvalPhase() {}

  // FACTORY PHASE ////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeFactoryPhase(Command<? extends T> resolvedCommand,
      Map<String, Object> state) {}

  default <T> void afterFactoryPhase(Command<? extends T> resolvedCommand,
      Map<String, Object> state, T instance) {}

  default <T> void catchFactoryPhase(Throwable problem) {}

  default <T> void finallyFactoryPhase() {}
}
