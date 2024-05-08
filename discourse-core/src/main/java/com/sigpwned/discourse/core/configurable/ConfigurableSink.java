package com.sigpwned.discourse.core.configurable;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ConfigurableSink {

  private final String name;
  private final Type genericType;
  private final List<Annotation> annotations;
  private final Map<Object, String> coordinates;

  public ConfigurableSink(String name, Type genericType, List<Annotation> annotations,
      Map<Object, String> coordinates) {
    this.name = requireNonNull(name);
    this.genericType = requireNonNull(genericType);
    this.annotations = unmodifiableList(annotations);
    this.coordinates = unmodifiableMap(coordinates);
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

  public Map<Object, String> getCoordinates() {
    return coordinates;
  }
}
