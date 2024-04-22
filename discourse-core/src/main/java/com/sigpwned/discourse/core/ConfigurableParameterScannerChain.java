package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.util.Chains;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigurableParameterScannerChain extends Chain<ConfigurableParameterScanner> {

  public List<ConfigurationParameter> scanForParameters(Class<?> type) {
    Map<String, List<ConfigurationParameter>> parameters = Chains.stream(this)
        .flatMap(scanner -> scanner.scanForParameters(type).stream())
        .collect(Collectors.groupingBy(ConfigurationParameter::getName));

    for (Map.Entry<String, List<ConfigurationParameter>> entry : parameters.entrySet()) {
      if (entry.getValue().size() > 1) {
        // TODO better exception
        throw new IllegalArgumentException(
            "Multiple parameters with the same name: " + entry.getKey());
      }
    }

    return parameters.values().stream().map(psi -> psi.get(0))
        .sorted(Comparator.comparing(ConfigurationParameter::getName)).toList();
  }
}
