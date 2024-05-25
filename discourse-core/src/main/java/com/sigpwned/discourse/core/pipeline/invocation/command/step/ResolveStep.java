package com.sigpwned.discourse.core.pipeline.invocation.command.step;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.pipeline.invocation.command.CommandInvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.command.CommandResolver;
import com.sigpwned.discourse.core.pipeline.invocation.command.model.CommandResolution;

public class ResolveStep {
  public <T> Optional<CommandResolution<? extends T>> resolve(CommandResolver<T> resolver,
      List<String> args, CommandInvocationContext context) {
    return resolver.resolveCommand(args, context);
  }
}
