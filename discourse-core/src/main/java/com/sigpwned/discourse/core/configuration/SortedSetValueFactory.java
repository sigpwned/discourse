package com.sigpwned.discourse.core.configuration;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configuration.model.ConfigurationArguments;
import java.util.Set;
import java.util.TreeSet;

public class SortedSetValueFactory implements ValueFactory {

  private final String name;

  public SortedSetValueFactory(String name) {
    this.name = requireNonNull(name);
  }

  @Override
  public Set<?> createValue(ConfigurationArguments args, ValueFactory vf) {
    return unmodifiableSet(new TreeSet<>(args.getValues(name)));
  }

  public String getName() {
    return name;
  }
}
