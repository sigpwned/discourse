package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;

public interface DiscourseListener {

  default void beforeScan(Class<?> clazz) {
  }

  default <T> void afterScan(Command<T> rootCommand) {
  }

  default <T> void beforeResolve(Command<T> rootCommand, List<String> args) {
  }

  default <T> void afterResolveBeforeParse(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArgs) {
  }

  default <T> void afterParseBeforeDeserialize(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<ParsedArgument> parsedArguments) {
  }

  default <T> void afterDeserializeBeforePrepare(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand,
      List<DeserializedArgument> deserializedArguments) {
  }

  default <T> void afterPrepareBeforeBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments) {
  }

  default <T> void afterBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments,
      T instance) {
  }
}
