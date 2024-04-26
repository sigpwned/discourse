package com.sigpwned.discourse.core.listener;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.model.argument.DeserializedArgument;
import com.sigpwned.discourse.core.model.argument.ParsedArgument;
import com.sigpwned.discourse.core.model.argument.PreparedArgument;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import java.util.List;

public interface DiscourseListener {

  default void beforeScan(Class<?> clazz, InvocationContext context) {
  }

  default <T> void afterScan(Command<T> rootCommand, InvocationContext context) {
  }

  default <T> void beforeResolve(Command<T> rootCommand, List<String> args,
      InvocationContext context) {
  }

  default <T> void beforeParse(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<String> remainingArgs,
      InvocationContext context) {
  }

  default <T> void beforeDeserialize(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<ParsedArgument> parsedArguments,
      InvocationContext context) {
  }

  default <T> void beforePrepare(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand,
      List<DeserializedArgument> deserializedArguments, InvocationContext context) {
  }

  default <T> void beforeBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments,
      InvocationContext context) {
  }

  default <T> void afterBuild(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, List<PreparedArgument> sinkedArguments,
      T instance, InvocationContext context) {
  }
}
