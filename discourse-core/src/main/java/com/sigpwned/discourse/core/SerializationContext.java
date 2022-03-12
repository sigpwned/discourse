package com.sigpwned.discourse.core;

import static java.util.Collections.unmodifiableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SerializationContext {
  private final LinkedList<ValueDeserializerFactory<?>> deserializers;

  public SerializationContext() {
    deserializers = new LinkedList<>();
  }

  public Optional<ValueDeserializer<?>> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    return deserializers.stream().filter(d -> d.isDeserializable(genericType, annotations))
        .findFirst().map(f -> f.getDeserializer(genericType, annotations));
  }

  public void addFirst(ValueDeserializerFactory<?> deserializer) {
    deserializers.remove(deserializer);
    deserializers.addFirst(deserializer);
  }

  public void addLast(ValueDeserializerFactory<?> deserializer) {
    deserializers.remove(deserializer);
    deserializers.addLast(deserializer);
  }

  public List<ValueDeserializerFactory<?>> getDeserializers() {
    return unmodifiableList(deserializers);
  }
}
