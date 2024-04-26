package com.sigpwned.discourse.core.configurable;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactory;
import java.util.List;
import java.util.stream.Stream;

public class ConfigurableClass<T> {

  private final Class<T> clazz;
  private final ConfigurableInstanceFactory<T> instanceFactory;
  private final List<ConfigurableComponent> instanceComponents;

  public ConfigurableClass(Class<T> clazz, ConfigurableInstanceFactory<T> instanceFactory,
      List<ConfigurableComponent> instanceComponents) {
    this.clazz = requireNonNull(clazz);
    this.instanceFactory = requireNonNull(instanceFactory);
    this.instanceComponents = unmodifiableList(instanceComponents);
  }

  public Class<T> getClazz() {
    return clazz;
  }

  public ConfigurableInstanceFactory<T> getInstanceFactory() {
    return instanceFactory;
  }

  public List<ConfigurableComponent> getInstanceComponents() {
    return instanceComponents;
  }

  public List<ConfigurableComponent> getComponents() {
    return Stream.concat(instanceFactory.getInputs().stream(), instanceComponents.stream())
        .toList();
  }
}
