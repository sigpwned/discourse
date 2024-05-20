package com.sigpwned.discourse.core.invocation.phase.scan;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.Chain;

public class SubCommandScannerChain extends Chain<SubCommandScanner> implements SubCommandScanner {

  private final List<SubCommandScanner> delegates;

  public SubCommandScannerChain(List<SubCommandScanner> delegates) {
    this.delegates = unmodifiableList(delegates);
  }

  @Override
  public <T> Optional<List<Map.Entry<String, Class<? extends T>>>> scanForSubCommands(
      Class<T> clazz) {
    for (SubCommandScanner delegate : getDelegates()) {
      List<Map.Entry<String, Class<? extends T>>> subcommands =
          delegate.scanForSubCommands(clazz).orElse(null);
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
