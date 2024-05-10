package com.sigpwned.discourse.core.invocation.phase.scan.impl;

import java.util.Map;

/**
 * Scans a class for subcommands.
 */
public interface SubCommandScanner {

  public <T> Map<String, Class<? extends T>> scanForSubCommands(Class<T> clazz);
}
