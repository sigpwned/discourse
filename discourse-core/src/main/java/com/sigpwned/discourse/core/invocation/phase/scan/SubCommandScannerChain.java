package com.sigpwned.discourse.core.invocation.phase.scan;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.util.Maybe;

public class SubCommandScannerChain extends Chain<SubCommandScanner> implements SubCommandScanner {
  @Override
  public <T> Maybe<List<Map.Entry<String, Class<? extends T>>>> scanForSubCommands(Class<T> clazz) {
    for (SubCommandScanner delegate : this) {
      Maybe<List<Map.Entry<String, Class<? extends T>>>> maybeSubcommands =
          delegate.scanForSubCommands(clazz);
      if (maybeSubcommands.isDecided()) {
        return maybeSubcommands;
      }
    }
    return Maybe.maybe();
  }
}
