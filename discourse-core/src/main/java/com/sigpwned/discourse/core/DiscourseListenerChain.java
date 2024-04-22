package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;

public class DiscourseListenerChain extends Chain<DiscourseListener> implements DiscourseListener {

  @Override
  public void beforeScan(Class<?> clazz) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforeScan(clazz);
    }
  }

  @Override
  public <T> void afterScan(Command<T> rootCommand) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.afterScan(rootCommand);
    }
  }

  @Override
  public <T> void beforeResolve(Command<T> rootCommand, List<String> args) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforeResolve(rootCommand, args);
    }
  }

  @Override
  public <T> void afterResolveBeforeParse(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArgs) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.afterResolveBeforeParse(rootCommand, dereferencedCommands, resolvedCommand,
          remainingArgs);
    }
  }

  @Override
  public <T> void afterParseBeforeDeserialize(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<ParsedArgument> parsedArguments) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.afterParseBeforeDeserialize(rootCommand, dereferencedCommands,
          resolvedCommand, parsedArguments);
    }
  }

  @Override
  public <T> void afterDeserializeBeforePrepare(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand,
      List<DeserializedArgument> deserializedArguments) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.afterDeserializeBeforePrepare(rootCommand, dereferencedCommands,
          resolvedCommand, deserializedArguments);
    }
  }

  @Override
  public <T> void afterPrepareBeforeBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.afterPrepareBeforeBuild(rootCommand, dereferencedCommands, resolvedCommand,
          sinkedArguments);
    }
  }

  @Override
  public <T> void afterBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments,
      T instance) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.afterBuild(rootCommand, dereferencedCommands, resolvedCommand,
          sinkedArguments, instance);
    }
  }
}
