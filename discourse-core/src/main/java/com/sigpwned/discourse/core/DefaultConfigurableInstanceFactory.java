package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public class DefaultConfigurableInstanceFactory<T> implements ConfigurableInstanceFactory<T> {

  private static interface InstanceCreator<T> {

    public static <T> InstanceCreator<T> fromExecutable(Class<T> rawType, Executable creator) {
      InstanceCreator<?> result;
      if (creator instanceof java.lang.reflect.Method creatorMethod) {
        result = new FactoryMethodInstanceCreator<>(creatorMethod);
      } else if (creator instanceof java.lang.reflect.Constructor<?> creatorConstructor) {
        result = new ConstructorInstanceCreator<>(creatorConstructor);
      } else {
        throw new IllegalArgumentException("Creator must be either a method or a constructor");
      }

      if (!result.getReturnType().equals(rawType)) {
        throw new IllegalArgumentException("Creator must return an instance of the class");
      }

      return (InstanceCreator<T>) result;
    }

    T invoke(List<Object> arguments)
        throws InvocationTargetException, InstantiationException, IllegalAccessException;

    public Class<?> getReturnType();

    int getParameterCount();
  }

  private static class ConstructorInstanceCreator<T> implements
      DefaultConfigurableInstanceFactory.InstanceCreator<T> {

    private final java.lang.reflect.Constructor<T> constructor;

    public ConstructorInstanceCreator(java.lang.reflect.Constructor<T> constructor) {
      if (!Modifier.isPublic(constructor.getModifiers())) {
        throw new IllegalArgumentException("Creator constructor must be public");
      }
      this.constructor = requireNonNull(constructor);
    }

    @Override
    public T invoke(List<Object> arguments)
        throws InvocationTargetException, InstantiationException, IllegalAccessException {
      return constructor.newInstance(arguments.toArray());
    }

    @Override
    public int getParameterCount() {
      return constructor.getParameterCount();
    }

    @Override
    public Class<?> getReturnType() {
      return constructor.getDeclaringClass();
    }
  }

  private static class FactoryMethodInstanceCreator<T> implements
      DefaultConfigurableInstanceFactory.InstanceCreator<T> {

    private final java.lang.reflect.Method method;

    public FactoryMethodInstanceCreator(java.lang.reflect.Method method) {
      if (!Modifier.isPublic(method.getModifiers())) {
        throw new IllegalArgumentException("Creator method must be public");
      }
      if (!Modifier.isStatic(method.getModifiers())) {
        throw new IllegalArgumentException("Creator method must be static");
      }
      this.method = requireNonNull(method);
    }

    @Override
    public T invoke(List<Object> arguments)
        throws InvocationTargetException, IllegalAccessException {
      return (T) method.invoke(null, arguments.toArray());
    }

    @Override
    public int getParameterCount() {
      return method.getParameterCount();
    }

    @Override
    public Class<?> getReturnType() {
      return method.getReturnType();
    }
  }


  private final Class<T> rawType;

  /**
   * Must be either a public constructor or a public static method that returns an instance of the
   * class.
   */
  private final InstanceCreator<T> creator;

  private final List<ConfigurationParameter> definedParameters;

  private final List<String> creatorParameters;

  private final Map<String, BiConsumer<? super T, Object>> instanceParameters;


  public DefaultConfigurableInstanceFactory(Class<T> rawType, Executable creatorExecutable,
      List<ConfigurationParameter> definedParameters, List<String> creatorParameters,
      Map<String, BiConsumer<? super T, Object>> instanceParameters) {
    this.rawType = requireNonNull(rawType);
    this.creator = InstanceCreator.fromExecutable(rawType, creatorExecutable);
    this.definedParameters = requireNonNull(definedParameters);
    this.creatorParameters = requireNonNull(creatorParameters);
    this.instanceParameters = unmodifiableMap(instanceParameters);
    if (creator.getParameterCount() != creatorParameters.size()) {
      throw new IllegalArgumentException(
          "Creator must have the same number of parameters as creatorParameterNames");
    }
    if (!creator.getReturnType().equals(rawType)) {
      throw new IllegalArgumentException("Creator must return an instance of the class");
    }
  }

  @Override
  public Set<String> getRequiredParameterNames() {
    return Set.copyOf(creatorParameters);
  }

  @Override
  public List<ConfigurationParameter> getDefinedParameters() {
    return unmodifiableList(definedParameters);
  }

  @Override
  public T createInstance(Map<String, Object> arguments) {
    List<Object> creatorArguments = creatorParameters.stream()
        .map(name -> Optional.ofNullable(arguments.get(name)).orElseThrow()).toList();

    T instance;
    try {
      instance = creator.invoke(creatorArguments);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }

    for (Map.Entry<String, BiConsumer<? super T, Object>> e : instanceParameters.entrySet()) {
      String propertyName = e.getKey();
      BiConsumer<? super T, Object> setter = e.getValue();
      Object value = arguments.get(propertyName);
      if (value != null) {
        setter.accept(instance, value);
      }
    }

    return instance;
  }
}
