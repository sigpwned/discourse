package com.sigpwned.discourse.core.configurable;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public class ConfigurableSource {

  private final String name;
  private final Type genericType;
  private final List<Annotation> annotations;

  public ConfigurableSource(String name, Type genericType, List<Annotation> annotations) {
    this.name = requireNonNull(name);
    this.genericType = requireNonNull(genericType);
    this.annotations = unmodifiableList(annotations);
  }

  public String getName() {
    return name;
  }

  public Type getGenericType() {
    return genericType;
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }
}
