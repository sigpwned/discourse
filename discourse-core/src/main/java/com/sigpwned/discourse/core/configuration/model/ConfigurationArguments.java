package com.sigpwned.discourse.core.configuration.model;

import static java.util.Collections.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record ConfigurationArguments(Map<String, List<?>> values) {

  public ConfigurationArguments {
    values = unmodifiableMap(values);
  }

  public Set<String> keySet() {
    return values.keySet();
  }

  public List<?> getValues(String name) {
    return Optional.ofNullable(values.get(name)).orElseGet(List::of);
  }

  public int size() {
    return values.size();
  }
}
