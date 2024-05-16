package com.sigpwned.discourse.core.invocation.phase.scan.impl;

import static java.util.Collections.*;

import com.sigpwned.discourse.core.Chain;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SubCommandScannerChain extends Chain<SubCommandScanner> implements SubCommandScanner {

  private final List<SubCommandScanner> delegates;

  public SubCommandScannerChain(List<SubCommandScanner> delegates) {
    this.delegates = unmodifiableList(delegates);
  }

  @Override
  public <T> Optional<Map<String, Class<? extends T>>> scanForSubCommands(Class<T> clazz) {
    for (SubCommandScanner delegate : getDelegates()) {
      Map<String, Class<? extends T>> subcommands = delegate.scanForSubCommands(clazz).orElse(null);
      if (subcommands != null) {
        return Optional.of(subcommands);
      }
    }
    return Optional.empty();
  }

  private List<SubCommandScanner> getDelegates() {
    return delegates;
  }
}
