package com.sigpwned.discourse.core.command2;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public sealed interface Command<T> permits RootCommand, SubCommand {

  Class<T> getClazz();

  Optional<String> getDescription();

  Optional<Function<Map<String, Object>, ? extends T>> getBody();

  Map<String, SubCommand<? extends T>> getSubcommands();
}
