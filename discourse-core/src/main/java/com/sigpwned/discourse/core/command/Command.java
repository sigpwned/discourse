package com.sigpwned.discourse.core.command;

import java.util.Map;
import java.util.Optional;

public sealed interface Command<T> permits RootCommand, SubCommand {

  Class<T> getClazz();

  Optional<String> getDescription();

  Optional<CommandBody<T>> getBody();

  Map<String, SubCommand<? extends T>> getSubcommands();
}
