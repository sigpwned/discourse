package com.sigpwned.discourse.core.invocation.phase.resolve;

import java.util.List;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;

public interface ResolvePhaseListener {

  default <T> void beforeResolvePhaseResolveStep(RootCommand<T> rootCommand,
      List<String> fullArgs) {}

  default <T> void afterResolvePhaseResolveStep(RootCommand<T> rootCommand, List<String> fullArgs,
      List<CommandDereference<? extends T>> commandDereferences,
      Command<? extends T> resolvedCommand, List<String> remainingArgs) {}

  default <T> void catchResolvePhaseResolveStep(Throwable problem) {}

  default <T> void finallyResolvePhaseResolveStep() {}
}
