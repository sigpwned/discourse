package com.sigpwned.discourse.core.command;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Optional;

public class ResolvedCommand<T> {
  private final String name;
  private final String version;
  private final List<ParentCommand> parents;
  private final Command<T> command;

  public ResolvedCommand(String name, String version, Command<T> model) {
    this(name, version, emptyList(), model);
  }

  public ResolvedCommand(String name, String version, List<ParentCommand> parents,
      Command<T> model) {
    this.name = name;
    this.version = version;
    this.parents = unmodifiableList(parents);
    this.command = requireNonNull(model);
  }

  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  public Optional<String> getVersion() {
    return Optional.ofNullable(version);
  }

  public List<ParentCommand> getParents() {
    return parents;
  }

  public Command<T> getCommand() {
    return command;
  }
}
