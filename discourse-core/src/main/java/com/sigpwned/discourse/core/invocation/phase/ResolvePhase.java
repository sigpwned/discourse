package com.sigpwned.discourse.core.invocation.phase;

import com.sigpwned.discourse.core.invocation.model.command.RootCommand;
import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandResolution;
import java.util.List;

public interface ResolvePhase {

  public <T> CommandResolution<? extends T> resolveCommand(RootCommand<T> rootCommand,
      List<String> args);
}
