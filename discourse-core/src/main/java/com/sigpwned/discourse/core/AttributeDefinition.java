package com.sigpwned.discourse.core;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public record AttributeDefinition(String name, Annotation annotation, Class<?> rawType,
    Type genericType) {

  public AttributeDefinition {
    name = requireNonNull(name);
    annotation = requireNonNull(annotation);
    rawType = requireNonNull(rawType);
    genericType = requireNonNull(genericType);
  }
}
