package com.sigpwned.discourse.core.command.tree;

import static java.util.Collections.unmodifiableMap;
import java.util.Map;

public final class SuperCommand<T> extends Command<T> {
  private final Map<String, Command<? extends T>> subcommands;

  public SuperCommand(String description, Map<String, Command<? extends T>> subcommands) {
    super(description);
    this.subcommands = unmodifiableMap(subcommands);
  }

  public Map<String, Command<? extends T>> getSubcommands() {
    return this.subcommands;
  }
}
