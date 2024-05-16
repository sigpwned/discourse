package com.sigpwned.discourse.core.invocation.phase.resolve;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.model.CommandResolution;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultCommandResolver implements CommandResolver {

  @Override
  public <T> CommandResolution<? extends T> resolveCommand(RootCommand<T> rootCommand,
      List<String> args) {

    Command<? extends T> command = rootCommand;
    List<CommandDereference<? extends T>> dereferences = new ArrayList<>();
    Iterator<String> iterator = args.iterator();
    while (iterator.hasNext()) {
      String arg = iterator.next();
      Command<? extends T> subcommand = command.getSubcommands().get(arg);
      if (subcommand == null) {
        break;
      }

      dereferences.add(new CommandDereference<>(subcommand, arg));

      command = subcommand;

      iterator.remove();
    }

    return new CommandResolution(rootCommand, command, dereferences, args);
  }
}
