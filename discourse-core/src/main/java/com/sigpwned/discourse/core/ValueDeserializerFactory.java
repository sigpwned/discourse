package com.sigpwned.discourse.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public interface ValueDeserializerFactory<T> {
  public boolean isDeserializable(Type genericType, List<Annotation> annotations);

  public ValueDeserializer<T> getDeserializer(Type genericType, List<Annotation> annotations);
}
