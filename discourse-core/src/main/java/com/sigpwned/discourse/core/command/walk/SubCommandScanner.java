package com.sigpwned.discourse.core.command.walk;

import java.util.Map;
import java.util.Optional;

/**
 * Scans a class for subcommands.
 */
public interface SubCommandScanner {

  public <T> Optional<Map<String, Class<? extends T>>> scanForSubCommands(Class<T> clazz);
}