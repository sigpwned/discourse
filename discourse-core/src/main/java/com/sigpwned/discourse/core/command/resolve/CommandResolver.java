package com.sigpwned.discourse.core.command.resolve;

import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandResolution;
import java.util.List;

public interface CommandResolver {

  public <T> CommandResolution<? extends T> resolveCommand(RootCommand<T> rootCommand,
      List<String> args);
}
