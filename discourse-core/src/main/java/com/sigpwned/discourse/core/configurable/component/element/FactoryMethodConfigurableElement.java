package com.sigpwned.discourse.core.configurable.component.element;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

public class FactoryMethodConfigurableElement implements ConfigurableElement {

  private final Method factoryMethod;

  public FactoryMethodConfigurableElement(Method factoryMethod) {
    this.factoryMethod = requireNonNull(factoryMethod);
    if (!Modifier.isPublic(factoryMethod.getModifiers())) {
      throw new IllegalArgumentException("factory method must be public");
    }
    if (!Modifier.isStatic(factoryMethod.getModifiers())) {
      throw new IllegalArgumentException("factory method must be static");
    }
    if (factoryMethod.getReturnType() == void.class) {
      throw new IllegalArgumentException("factory method must have a non-void return type");
    }
    if (!factoryMethod.getDeclaringClass().isAssignableFrom(factoryMethod.getReturnType())) {
      throw new IllegalArgumentException(
          "factory method must return a type that is assignable to the declaring class");
    }
  }

  @Override
  public String getName() {
    return INSTANCE_NAME;
  }

  @Override
  public Type getGenericType() {
    return getFactoryMethod().getReturnType();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getFactoryMethod().getAnnotations());
  }

  private Method getFactoryMethod() {
    return factoryMethod;
  }
}
