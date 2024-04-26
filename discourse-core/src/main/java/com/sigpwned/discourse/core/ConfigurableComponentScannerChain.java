package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.util.Chains;
import java.util.List;

public class ConfigurableComponentScannerChain extends Chain<ConfigurableComponentScanner> {

  public List<ConfigurableComponent> scanForComponents(Class<?> rawType,
      InvocationContext context) {
    return Chains.stream(this)
        .flatMap(scanner -> scanner.scanForComponents(rawType, context).stream()).toList();
  }
}
