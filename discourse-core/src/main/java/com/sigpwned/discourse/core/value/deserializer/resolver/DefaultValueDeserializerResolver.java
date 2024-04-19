package com.sigpwned.discourse.core.value.deserializer.resolver;

import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;
import com.sigpwned.discourse.core.ValueDeserializerResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DefaultValueDeserializerResolver implements ValueDeserializerResolver {

  public static class Builder {

    private final DefaultValueDeserializerResolver resolver;

    public Builder() {
      resolver = new DefaultValueDeserializerResolver();
    }

    public Builder addFirst(ValueDeserializerFactory<?> deserializer) {
      resolver.addFirst(deserializer);
      return this;
    }

    public Builder addLast(ValueDeserializerFactory<?> deserializer) {
      resolver.addLast(deserializer);
      return this;
    }

    public DefaultValueDeserializerResolver build() {
      return resolver;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final LinkedList<ValueDeserializerFactory<?>> deserializers;

  public DefaultValueDeserializerResolver() {
    deserializers = new LinkedList<>();
  }

  @Override
  public Optional<ValueDeserializer<?>> resolveValueDeserializer(Type genericType,
      List<Annotation> annotations) {
    return getDeserializers().stream().filter(d -> d.isDeserializable(genericType, annotations))
        .findFirst().map(f -> f.getDeserializer(genericType, annotations));
  }

  @Override
  public void addFirst(ValueDeserializerFactory<?> deserializer) {
    getDeserializers().remove(deserializer);
    getDeserializers().addFirst(deserializer);
  }

  @Override
  public void addLast(ValueDeserializerFactory<?> deserializer) {
    getDeserializers().remove(deserializer);
    getDeserializers().addLast(deserializer);
  }

  private LinkedList<ValueDeserializerFactory<?>> getDeserializers() {
    return deserializers;
  }
}
