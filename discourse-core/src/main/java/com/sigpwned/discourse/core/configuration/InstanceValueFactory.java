package com.sigpwned.discourse.core.configuration;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configuration.model.ConfigurationArguments;
import java.lang.reflect.Type;
import java.util.Map;

public class InstanceValueFactory implements ValueFactory {

  private final Map<String, Type> dependencies;
  // TODO This should be a sink factory
  private final InstanceBuilder.Factory builderFactory;

  public InstanceValueFactory(Map<String, Type> dependencies,
      InstanceBuilder.Factory builderFactory) {
    this.dependencies = unmodifiableMap(dependencies);
    this.builderFactory = requireNonNull(builderFactory);
  }

  @Override
  public Object createValue(ConfigurationArguments args, ValueFactoryChain chain) {
    InstanceBuilder sink = getBuilderFactory().newInstanceBuilder();
    for (Map.Entry<String, Type> entry : getDependencies().entrySet()) {
      String name = entry.getKey();
      Type type = entry.getValue();
      ValueFactory factory = chain.findValueFactory(type).orElseThrow(() -> {
        // TODO better exception
        return new IllegalArgumentException("No factory for " + type);
      });
      // TODO should we knock off some prefix?
      Object value = factory.createValue(args, chain);
      sink.add(name, value);
    }
    return sink.build();
  }

  private Map<String, Type> getDependencies() {
    return dependencies;
  }

  private InstanceBuilder.Factory getBuilderFactory() {
    return builderFactory;
  }
}
