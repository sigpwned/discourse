package com.sigpwned.discourse.core.invocation.phase.scan;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Scans a class for subcommands.
 */
public interface SubCommandScanner {

  public <T> Optional<List<Map.Entry<String, Class<? extends T>>>> scanForSubCommands(
      Class<T> clazz);
}
