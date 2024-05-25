package com.sigpwned.discourse.core.command.builder;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.command.RootCommand;

public class RootCommandBuilder<T> {
  private final Class<T> clazz;
  private String name;
  private String description;
  private String version;

  public RootCommandBuilder(Class<T> clazz) {
    this.clazz = requireNonNull(clazz);
  }

  public Class<T> clazz() {
    return clazz;
  }

  public RootCommandBuilder<T> name(String name) {
    this.name = name;
    return this;
  }

  public String name() {
    return name;
  }

  public RootCommandBuilder<T> description(String description) {
    this.description = description;
    return this;
  }

  public String description() {
    return description;
  }

  public RootCommandBuilder<T> version(String version) {
    this.version = version;
    return this;
  }

  public String version() {
    return version;
  }

  public RootCommand<T> build() {
    return new RootCommand<>(clazz, name, description, version);
  }
}
