package com.sigpwned.discourse.core.configuration;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.model.coordinate.Coordinate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public record ConfigurationParameter(String name, boolean required, Set<Coordinate> coordinates, Type genericType,
    List<Annotation> annotations) {

  public ConfigurationParameter {
    name = requireNonNull(name);
    coordinates = unmodifiableSet(coordinates);
    genericType = requireNonNull(genericType);
    annotations = unmodifiableList(annotations);
  }
}
