package com.sigpwned.discourse.core.invocation.model.command;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public sealed interface Command<T> permits RootCommand, SubCommand {

  Class<T> getClazz();

  Optional<String> getDescription();

  Optional<Function<Map<String, Object>, ? extends T>> getBody();

  Map<String, SubCommand<? extends T>> getSubcommands();
}
