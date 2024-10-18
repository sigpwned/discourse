package com.sigpwned.discourse.core.pipeline.invocation.step.resolve;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.resolve.model.CommandResolution;

public interface CommandResolver<T> {
  public Optional<CommandResolution<? extends T>> resolveCommand(List<String> args,
      InvocationContext context);
}
