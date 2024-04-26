package com.sigpwned.discourse.core.configurable.instance.factory;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.ConfigurableInstanceFactoryProvider;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultConstructorConfigurableInstanceFactory<T> implements
    ConfigurableInstanceFactory<T> {

  public static class Provider implements ConfigurableInstanceFactoryProvider {

    @Override
    public <T> Optional<ConfigurableInstanceFactory<T>> getConfigurationInstanceFactory(
        Class<T> type) {
      Constructor<T> defaultConstructor;
      try {
        defaultConstructor = type.getConstructor();
      } catch (NoSuchMethodException e) {
        return Optional.empty();
      }
      if(!Modifier.isPublic(defaultConstructor.getModifiers())) {
        return Optional.empty();
      }
      return Optional.of(new DefaultConstructorConfigurableInstanceFactory<>(defaultConstructor));
    }
  }

  private final Constructor<T> defaultConstructor;

  public DefaultConstructorConfigurableInstanceFactory(Constructor<T> defaultConstructor) {
    this.defaultConstructor = requireNonNull(defaultConstructor);
    if (defaultConstructor.getParameterCount() != 0) {
      throw new IllegalArgumentException("Default constructor must have no parameters");
    }
    if (!Modifier.isPublic(defaultConstructor.getModifiers())) {
      throw new IllegalArgumentException("Default constructor must be accessible");
    }
  }

  @Override
  public Set<String> getRequiredParameterNames() {
    // The default constructor does not require any parameters, by definition
    return Set.of();
  }

  @Override
  public List<ConfigurationParameter> getDefinedParameters() {
    // If a constructor defines parameters, then they are from constructor parameters.
    // The default constructor has no parameters, so cannot define any parameters, either.
    return List.of();
  }

  @Override
  public T createInstance(Map<String, Object> arguments) {
    try {
      return defaultConstructor.newInstance();
    } catch (ReflectiveOperationException e) {
      // TODO better exception
      throw new RuntimeException(e);
    }
  }
}
