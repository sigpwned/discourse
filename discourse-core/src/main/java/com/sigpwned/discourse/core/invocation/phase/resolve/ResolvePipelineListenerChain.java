package com.sigpwned.discourse.core.invocation.phase.resolve;

import com.sigpwned.discourse.core.chain.Chain;
import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.scan.Command;
import com.sigpwned.discourse.core.invocation.phase.scan.RootCommand;
import java.util.List;

public class ResolvePipelineListenerChain extends Chain<ResolvePipelineListener> implements
    ResolvePipelineListener {

  @Override
  public <T> void beforeResolveCommand(RootCommand<T> root, List<String> args) {
    for (ResolvePipelineListener listener : this) {
      listener.beforeResolveCommand(root, args);
    }
  }

  @Override
  public <T, R extends T> void afterResolveCommand(RootCommand<T> root, List<String> args,
      Command<R> subcommand, List<CommandDereference<? super R>> commandDereferences,
      List<String> remainingArgs) {
    for (ResolvePipelineListener listener : this) {
      listener.afterResolveCommand(root, args, subcommand, commandDereferences, remainingArgs);
    }
  }
}
