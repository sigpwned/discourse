package com.sigpwned.discourse.core.command2.impl;

import static com.sigpwned.discourse.core.util.MoreCollectors.entriesToMap;
import static java.util.Collections.*;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.util.Discriminators;
import com.sigpwned.discourse.core.util.Streams;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Scans a class for subcommands. This implementation uses the
 * {@link Configurable#subcommands() @Configurable} annotation to find subcommands.
 */
public class ConfigurableSubCommandScanner implements SubCommandScanner {

  @Override
  public <T> Map<String, Class<? extends T>> scanForSubCommands(Class<T> clazz) {
    Configurable annotation = clazz.getAnnotation(Configurable.class);
    if (annotation == null) {
      return emptyMap();
    }

    if (annotation.subcommands().length == 0) {
      return emptyMap();
    }

    List<Map.Entry<String, Class<? extends T>>> subcommands = Arrays.stream(
        annotation.subcommands()).peek(subcommand -> {
      // Is the discriminator valid?
      if (!Discriminators.isValid(subcommand.discriminator())) {
        throw new IllegalArgumentException("Invalid discriminator: " + subcommand.discriminator());
      }
    }).peek(subcommand -> {
      // Is the subcommand a subclass of the configurable class?
      if (!clazz.isAssignableFrom(subcommand.configurable())) {
        throw new IllegalArgumentException(
            "Subcommand " + subcommand.configurable() + " is not a subclass of " + clazz);
      }
    }).map(subcommand -> {
      // This cast is safe because we checked it above.
      return Map.<String, Class<? extends T>>entry(subcommand.discriminator(),
          (Class<? extends T>) subcommand.configurable());
    }).toList();

    if (Streams.duplicates(subcommands.stream().map(Map.Entry::getKey)).findAny().isPresent()) {
      throw new IllegalArgumentException("Duplicate discriminator");
    }

    return subcommands.stream().collect(entriesToMap());
  }
}
