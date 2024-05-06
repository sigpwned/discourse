package com.sigpwned.discourse.core.configurable.instance.factory.scanner;

import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.configurable.instance.factory.DefaultConstructorConfigurableInstanceFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Optional;

public class DefaultConstructorConfigurableInstanceFactoryScanner implements
    ConfigurableInstanceFactoryScanner {

  @Override
  public <T> Optional<ConfigurableInstanceFactory<T>> scanForInstanceFactory(Class<T> type) {
    Constructor<T> defaultConstructor;
    try {
      defaultConstructor = type.getConstructor();
    } catch (NoSuchMethodException e) {
      return Optional.empty();
    }
    if (!Modifier.isPublic(defaultConstructor.getModifiers())) {
      return Optional.empty();
    }
    return Optional.of(new DefaultConstructorConfigurableInstanceFactory<>(defaultConstructor));
  }
}
