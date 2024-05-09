package com.sigpwned.discourse.core.command2.impl;

import static java.util.Collections.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SubCommandScannerChain implements SubCommandScanner {

  private final List<SubCommandScanner> delegates;

  public SubCommandScannerChain(List<SubCommandScanner> delegates) {
    this.delegates = unmodifiableList(delegates);
  }

  @Override
  public <T> Map<String, Class<? extends T>> scanForSubCommands(Class<T> clazz) {
    Map<String, Class<? extends T>> result = new LinkedHashMap<>();
    for (SubCommandScanner delegate : getDelegates()) {
      Map<String, Class<? extends T>> subcommands = delegate.scanForSubCommands(clazz);
      for (Map.Entry<String, Class<? extends T>> entry : subcommands.entrySet()) {
        if (result.containsKey(entry.getKey())) {
          throw new IllegalArgumentException("Duplicate discriminator: " + entry.getKey());
        }
        result.put(entry.getKey(), entry.getValue());
      }
    }
    return unmodifiableMap(result);
  }

  private List<SubCommandScanner> getDelegates() {
    return delegates;
  }
}
