package com.sigpwned.discourse.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ValueDeserializer<T> {
  public boolean isDeserializable(Type genericType, Annotation[] annotations);

  public T deserialize(Type genericType, Annotation[] annotations, String value);
}
