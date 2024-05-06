package com.sigpwned.discourse.core.configurable.component.element;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;

public class ConstructorConfigurableElement implements ConfigurableElement {

  private final Constructor<?> constructor;

  public ConstructorConfigurableElement(Constructor<?> constructor) {
    this.constructor = requireNonNull(constructor);
  }

  @Override
  public String getName() {
    return INSTANCE_NAME;
  }

  @Override
  public Type getGenericType() {
    return getConstructor().getDeclaringClass();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getConstructor().getAnnotations());
  }

  private Constructor<?> getConstructor() {
    return constructor;
  }
}
