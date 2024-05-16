package com.sigpwned.discourse.core.invocation.phase;

import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandResolution;
import java.util.List;

public interface ResolvePhase {

  public <T> CommandResolution<? extends T> resolve(RootCommand<T> rootCommand,
      List<String> args);
}
