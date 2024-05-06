package com.sigpwned.discourse.core.configuration;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configuration.model.ConfigurationArguments;
import java.util.LinkedHashSet;
import java.util.Set;

public class SetValueFactory implements ValueFactory {

  private final String name;

  public SetValueFactory(String name) {
    this.name = requireNonNull(name);
  }

  @Override
  public Set<?> createValue(ConfigurationArguments args, ValueFactory vf) {
    return unmodifiableSet(new LinkedHashSet<>(args.getValues(name)));
  }

  public String getName() {
    return name;
  }
}
