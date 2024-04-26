package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.model.argument.DeserializedArgument;
import com.sigpwned.discourse.core.model.argument.ParsedArgument;
import com.sigpwned.discourse.core.model.argument.PreparedArgument;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import java.util.List;

public interface DiscourseListener {

  default void beforeScan(Class<?> clazz) {
  }

  default <T> void afterScan(Command<T> rootCommand) {
  }

  default <T> void beforeResolve(Command<T> rootCommand, List<String> args) {
  }

  default <T> void beforeParse(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArgs) {
  }

  default <T> void beforeDeserialize(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<ParsedArgument> parsedArguments) {
  }

  default <T> void beforePrepare(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand,
      List<DeserializedArgument> deserializedArguments) {
  }

  default <T> void beforeBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments) {
  }

  default <T> void afterBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments,
      T instance) {
  }
}
