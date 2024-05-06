package com.sigpwned.discourse.core.configuration;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configuration.model.ConfigurationArguments;
import com.sigpwned.discourse.core.util.MoreIterables;

public class ScalarValueFactory implements ValueFactory {

  private final String name;

  public ScalarValueFactory(String name) {
    this.name = requireNonNull(name);
  }

  @Override
  public Object createValue(ConfigurationArguments args, ValueFactory vf) {
    return MoreIterables.last(args.getValues(name)).orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("missing value for " + name);
    });
  }

  public String getName() {
    return name;
  }
}
