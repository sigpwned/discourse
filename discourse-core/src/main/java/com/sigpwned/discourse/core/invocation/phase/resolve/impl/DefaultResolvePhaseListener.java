package com.sigpwned.discourse.core.invocation.phase.resolve.impl;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import java.util.List;

public interface DefaultResolvePhaseListener {

  public <T> void beforeResolve(RootCommand<T> rootCommand, List<String> fullArgs);

  public <T> void afterResolve(RootCommand<T> rootCommand, List<String> fullArgs,
      List<CommandDereference<? extends T>> commandDereferences,
      Command<? extends T> resolvedCommand, List<String> remainingArgs);
}
