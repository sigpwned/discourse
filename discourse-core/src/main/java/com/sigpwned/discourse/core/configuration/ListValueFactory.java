package com.sigpwned.discourse.core.configuration;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configuration.model.ConfigurationArguments;
import java.util.List;

public class ListValueFactory implements ValueFactory {

  private final String name;

  public ListValueFactory(String name) {
    this.name = requireNonNull(name);
  }

  @Override
  public List<?> createValue(ConfigurationArguments args, ValueFactory vf) {
    return List.copyOf(args.getValues(name));
  }

  public String getName() {
    return name;
  }
}
