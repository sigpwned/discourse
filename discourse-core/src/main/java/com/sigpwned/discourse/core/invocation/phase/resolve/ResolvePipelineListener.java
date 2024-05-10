package com.sigpwned.discourse.core.invocation.phase.resolve;

import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.scan.Command;
import com.sigpwned.discourse.core.invocation.phase.scan.RootCommand;
import java.util.List;

public interface ResolvePipelineListener {

  default <T> void beforeResolveCommand(RootCommand<T> root, List<String> args) {
  }

  default <T, R extends T> void afterResolveCommand(RootCommand<T> root, List<String> args,
      Command<R> subcommand, List<CommandDereference<? super R>> dereferences,
      List<String> remainingArgs) {
  }
}
