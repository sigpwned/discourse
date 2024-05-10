package com.sigpwned.discourse.core.invocation.phase.resolve.impl;

import com.sigpwned.discourse.core.invocation.phase.resolve.ResolvePhase;
import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.resolve.model.ResolvedCommand;
import com.sigpwned.discourse.core.invocation.phase.scan.Command;
import com.sigpwned.discourse.core.invocation.phase.scan.RootCommand;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultResolvePhase implements ResolvePhase {

  @Override
  public <T> ResolvedCommand<? extends T> resolveCommand(RootCommand<T> rootCommand,
      List<String> args) {

    Command<? extends T> command = rootCommand;
    List<String> remainingArgs = new ArrayList<>(args);
    Iterator<String> iterator = remainingArgs.iterator();
    List<CommandDereference<? extends T>> dereferences = new ArrayList<>();
    while (iterator.hasNext()) {
      String nextArg = remainingArgs.get(0);

      Command<? extends T> subcommand = command.getSubcommands().get(nextArg);
      if (subcommand == null) {
        break;
      }

      dereferences.add(new CommandDereference<>(command, nextArg));

      command = subcommand;

      iterator.remove();
    }

    // If command is of type `T`, then we know the dereferences are of type `? super T`, since
    // a subcommand has to be a subtype of the parent. The compiler, however, does not know this,
    // so we use a raw type.
    return new ResolvedCommand(command, dereferences, remainingArgs);
  }
}
