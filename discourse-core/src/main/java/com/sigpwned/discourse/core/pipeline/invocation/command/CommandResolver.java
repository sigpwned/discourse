package com.sigpwned.discourse.core.pipeline.invocation.command;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.pipeline.invocation.command.model.CommandResolution;

public interface CommandResolver<T> {
  public Optional<CommandResolution<? extends T>> resolveCommand(List<String> args,
      CommandInvocationContext context);
}
