package com.sigpwned.discourse.core.pipeline.invocation.command.model;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.ResolvedCommand;

public class CommandResolution<T> {
  private final ResolvedCommand<T> command;
  private final List<String> args;

  public CommandResolution(ResolvedCommand<T> command, List<String> args) {
    this.command = requireNonNull(command);
    this.args = unmodifiableList(args);
  }

  public Command<T> getCommand() {
    return command.getCommand();
  }

  public List<String> getArgs() {
    return args;
  }
}
