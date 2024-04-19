package com.sigpwned.discourse.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public interface ValueDeserializerResolver {

  Optional<ValueDeserializer<?>> resolveValueDeserializer(Type genericType,
      List<Annotation> annotations);

  void addFirst(ValueDeserializerFactory<?> deserializer);

  void addLast(ValueDeserializerFactory<?> deserializer);
}
