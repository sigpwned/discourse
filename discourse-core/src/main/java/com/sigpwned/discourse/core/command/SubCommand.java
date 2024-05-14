package com.sigpwned.discourse.core.command;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class SubCommand<T> implements Command<T> {

  private final Class<T> clazz;
  private final String discriminator;
  private final String description;
  private final Function<Map<String, Object>, ? extends T> body;
  private final Map<String, SubCommand<? extends T>> subcommands;

  public SubCommand(Class<T> clazz, String discriminator, String description,
      Function<Map<String, Object>, ? extends T> body,
      Map<String, SubCommand<? extends T>> subcommands) {
    this.clazz = requireNonNull(clazz);
    this.discriminator = requireNonNull(discriminator);
    this.description = description;
    this.body = body;
    this.subcommands = unmodifiableMap(subcommands);
  }

  @Override
  public Class<T> getClazz() {
    return clazz;
  }


  public String getDiscriminator() {
    return discriminator;
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  @Override
  public Optional<Function<Map<String, Object>, ? extends T>> getBody() {
    return Optional.ofNullable(body);
  }

  @Override
  public Map<String, SubCommand<? extends T>> getSubcommands() {
    return subcommands;
  }
}
