package com.sigpwned.discourse.core.command;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;

public final class RootCommand<T> implements Command<T> {

  private final Class<T> clazz;
  private final String name;
  private final String version;
  private final String description;
  private final CommandBody<T> body;
  private final Map<String, SubCommand<? extends T>> subcommands;

  public RootCommand(Class<T> clazz, String name, String version, String description,
      CommandBody<T> body, Map<String, SubCommand<? extends T>> subcommands) {
    this.clazz = requireNonNull(clazz);
    this.name = name;
    this.version = version;
    this.description = description;
    this.body = body;
    this.subcommands = unmodifiableMap(subcommands);
  }

  @Override
  public Class<T> getClazz() {
    return clazz;
  }

  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  public Optional<String> getVersion() {
    return Optional.ofNullable(version);
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  @Override
  public Optional<CommandBody<T>> getBody() {
    return Optional.ofNullable(body);
  }

  @Override
  public Map<String, SubCommand<? extends T>> getSubcommands() {
    return subcommands;
  }
}
