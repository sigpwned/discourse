package com.sigpwned.discourse.core.invocation.phase.resolve;

import java.util.List;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandResolution;

public interface CommandResolver {

  public <T> CommandResolution<? extends T> resolveCommand(RootCommand<T> rootCommand,
      List<String> args);
}
