package com.sigpwned.discourse.core.invocation.phase.scan.configurable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.command.Discriminator;
import com.sigpwned.discourse.core.invocation.phase.scan.SubCommandScanner;
import com.sigpwned.discourse.core.util.Streams;

public class ConfigurableClassWalker {
  public static interface Listener<T> {
    public void root(Class<T> clazz, String name, String version, String description);

    public <U extends T> void subcommand(Class<U> superclazz, Discriminator discriminator,
        Class<? extends U> subclazz, String description);
  }

  public <T> void walk(Class<T> clazz, SubCommandScanner scanner, Listener<T> listener) {
    // TODO check discriminator?
    // TODO check no @Configurable parent?
    Configurable configurable = clazz.getAnnotation(Configurable.class);
    if (configurable == null) {
      // TODO better exception
      throw new IllegalArgumentException("No @Configurable annotation on " + clazz);
    }

    listener.root(clazz, configurable.name(), configurable.version(), configurable.description());

    List<Map.Entry<String, Class<? extends T>>> subcommands =
        scanner.scanForSubCommands(clazz).orElseGet(Collections::emptyList);
    if (Streams.duplicates(subcommands.stream().map(Map.Entry::getKey).filter(Objects::nonNull))
        .findAny().isPresent()) {
      // TODO better exception
      throw new IllegalArgumentException("Duplicate discriminator");
    }
    for (Map.Entry<String, Class<? extends T>> subcommand : subcommands) {
      subwalk(clazz, subcommand.getKey(), subcommand.getValue(), scanner, listener);
    }
  }

  protected <T, U extends T, V extends U> void subwalk(Class<U> superclazz,
      String expectedDiscriminator, Class<V> subclazz, SubCommandScanner scanner,
      Listener<T> listener) {
    Configurable configurable = subclazz.getAnnotation(Configurable.class);
    if (configurable == null) {
      // TODO better exception
      throw new IllegalArgumentException("No @Configurable annotation on " + subclazz);
    }

    if (!superclazz.isAssignableFrom(subclazz)) {
      // TODO better exception
      throw new IllegalArgumentException(
          "Subcommand " + subclazz + " is not a subclass of " + superclazz);
    }

    if (expectedDiscriminator != null
        && !expectedDiscriminator.equals(configurable.discriminator())) {
      // TODO better exception
      throw new IllegalArgumentException("Expected discriminator " + expectedDiscriminator
          + " but found " + configurable.discriminator());
    }

    Discriminator discriminator;
    try {
      discriminator = Discriminator.fromString(configurable.discriminator());
    } catch (IllegalArgumentException e) {
      // TODO better exception
      throw new IllegalArgumentException("Invalid discriminator: " + configurable.discriminator());
    }

    listener.subcommand(superclazz, discriminator, subclazz, configurable.description());

    List<Map.Entry<String, Class<? extends V>>> subcommands =
        scanner.scanForSubCommands(subclazz).orElseGet(Collections::emptyList);
    if (Streams.duplicates(subcommands.stream().map(Map.Entry::getKey).filter(Objects::nonNull))
        .findAny().isPresent()) {
      // TODO better exception
      throw new IllegalArgumentException("Duplicate discriminator");
    }
    for (Map.Entry<String, Class<? extends V>> subcommand : subcommands) {
      subwalk(subclazz, subcommand.getKey(), subcommand.getValue(), scanner, listener);
    }
  }
}
