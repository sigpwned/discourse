package com.sigpwned.discourse.core;

import static java.util.Collections.unmodifiableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.value.deserializer.BigDecimalValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.BooleanValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.ByteValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.CharValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.DoubleValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.FloatValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.InstantValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.IntValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LocalDateTimeValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LocalDateValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LocalTimeValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LongValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.ShortValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.StringValueDeserializerFactory;

public class SerializationContext {
  private final LinkedList<ValueDeserializerFactory<?>> deserializers;

  public SerializationContext() {
    deserializers = new LinkedList<>();
    addLast(StringValueDeserializerFactory.INSTANCE);
    addLast(LongValueDeserializerFactory.INSTANCE);
    addLast(IntValueDeserializerFactory.INSTANCE);
    addLast(CharValueDeserializerFactory.INSTANCE);
    addLast(ShortValueDeserializerFactory.INSTANCE);
    addLast(ByteValueDeserializerFactory.INSTANCE);
    addLast(DoubleValueDeserializerFactory.INSTANCE);
    addLast(FloatValueDeserializerFactory.INSTANCE);
    addLast(BigDecimalValueDeserializerFactory.INSTANCE);
    addLast(BooleanValueDeserializerFactory.INSTANCE);
    addLast(InstantValueDeserializerFactory.INSTANCE);
    addLast(LocalDateTimeValueDeserializerFactory.INSTANCE);
    addLast(LocalDateValueDeserializerFactory.INSTANCE);
    addLast(LocalTimeValueDeserializerFactory.INSTANCE);
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
