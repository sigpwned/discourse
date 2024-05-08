package com.sigpwned.discourse.core.configurable;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.configurable.component.scanner.ConfigurableCandidateComponentScanner;
import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.configurable.instance.factory.scanner.ConfigurableInstanceFactoryScanner;
import java.util.List;

public class ConfigurableClassScanner {

  private final ConfigurableInstanceFactoryScanner instanceFactoryScanner;
  private final ConfigurableCandidateComponentScanner componentScanner;

  public <T> ConfigurableClass<T> scan(Class<T> clazz) {
    // If we're not @Configurable, then this is not a valid class to scan.
    Configurable configurable = clazz.getAnnotation(Configurable.class);
    if (configurable == null) {
      throw new IllegalArgumentException(
          "Class %s is not @Configurable".formatted(clazz.getName()));
    }

    List<Object> candidateComponents

    // The first thing we need to do is compute the set of "components" for this class, which is all
    // inputs, fields, getters, and setters. The set of all components for this class could include
    // inputs to the factory. So before we can compute the set of components, we need to choose our
    // factory. This is an abstraction of a constructor or factory method.
    ConfigurableInstanceFactory<T> instanceFactory = getInstanceFactoryScanner().scanForInstanceFactory(
        clazz).orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("No factory for class %s".formatted(clazz.getName()));
    });

    // Now that we have our factory, we can compute the set of non-factory related components. That
    // is, fields, getters, and setters.
    List<ConfigurableComponent> instanceComponents = getComponentScanner().scanForComponents(clazz);

    return new ConfigurableClass<>(clazz, instanceFactory, instanceComponents);
  }

  private ConfigurableInstanceFactoryScanner getInstanceFactoryScanner() {
    return instanceFactoryScanner;
  }

  private ConfigurableCandidateComponentScanner getComponentScanner() {
    return componentScanner;
  }
}
