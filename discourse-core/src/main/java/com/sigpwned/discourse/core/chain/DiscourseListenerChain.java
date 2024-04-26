package com.sigpwned.discourse.core.chain;

import com.sigpwned.discourse.core.DiscourseListener;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.model.argument.DeserializedArgument;
import com.sigpwned.discourse.core.model.argument.ParsedArgument;
import com.sigpwned.discourse.core.model.argument.PreparedArgument;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
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
  public <T> void beforeParse(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArgs) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforeParse(rootCommand, dereferencedCommands, resolvedCommand,
          remainingArgs);
    }
  }

  @Override
  public <T> void beforeDeserialize(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<ParsedArgument> parsedArguments) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforeDeserialize(rootCommand, dereferencedCommands,
          resolvedCommand, parsedArguments);
    }
  }

  @Override
  public <T> void beforePrepare(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand,
      List<DeserializedArgument> deserializedArguments) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforePrepare(rootCommand, dereferencedCommands,
          resolvedCommand, deserializedArguments);
    }
  }

  @Override
  public <T> void beforeBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments) {
    for (DiscourseListener discourseListener : this) {
      discourseListener.beforeBuild(rootCommand, dereferencedCommands, resolvedCommand,
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
