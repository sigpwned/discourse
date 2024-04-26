package com.sigpwned.discourse.core.configurable.component;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public abstract sealed class ConfigurableComponent permits FieldConfigurableComponent,
    GetterConfigurableComponent, InputConfigurableComponent, SetterConfigurableComponent {

  private final String name;
  private final Class<?> rawType;
  private final Type genericType;
  private final List<Annotation> annotations;

  public ConfigurableComponent(String name, Class<?> rawType, Type genericType,
      List<Annotation> annotations) {
    this.name = requireNonNull(name);
    this.rawType = requireNonNull(rawType);
    this.genericType = requireNonNull(genericType);
    this.annotations = unmodifiableList(annotations);
  }

  public String getName() {
    return name;
  }

  public Class<?> getRawType() {
    return rawType;
  }

  public Type getGenericType() {
    return genericType;
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }
}
