package com.sigpwned.discourse.core.command.builder;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.command.SubCommand;

public class SubCommandBuilder<T> {
  private final Class<? super T> superclazz;
  private String discriminator;
  private final Class<T> clazz;
  private String description;

  public SubCommandBuilder(Class<? super T> superclazz, String discriminator, Class<T> clazz) {
    this.superclazz = requireNonNull(superclazz);
    this.discriminator = requireNonNull(discriminator);
    this.clazz = requireNonNull(clazz);
    if (!superclazz.isAssignableFrom(clazz)) {
      throw new IllegalArgumentException("The class " + clazz + " does not extend " + superclazz);
    }
  }

  public Class<? super T> superclazz() {
    return superclazz;
  }

  public SubCommandBuilder<T> discriminator(String discriminator) {
    this.discriminator = requireNonNull(discriminator);
    return this;
  }

  public String discriminator() {
    return discriminator;
  }

  public Class<T> clazz() {
    return clazz;
  }

  public SubCommandBuilder<T> description(String description) {
    this.description = description;
    return this;
  }

  public String description() {
    return description;
  }

  public SubCommand<T> build() {
    return new SubCommand<>(discriminator, clazz, description);
  }
}
