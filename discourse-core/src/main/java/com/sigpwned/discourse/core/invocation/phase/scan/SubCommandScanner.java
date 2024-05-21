package com.sigpwned.discourse.core.invocation.phase.scan;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.util.Maybe;

/**
 * Scans a class for subcommands.
 */
public interface SubCommandScanner {

  public <T> Maybe<List<Map.Entry<String, Class<? extends T>>>> scanForSubCommands(Class<T> clazz);
}
