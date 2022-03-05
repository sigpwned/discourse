package com.sigpwned.discourse.core;

import static java.util.Collections.unmodifiableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.value.deserializer.BooleanValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.ByteValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.CharValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.DoubleValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.FloatValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.InstantValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.IntValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.LocalDateTimeValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.LocalDateValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.LocalTimeValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.LongValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.ShortValueDeserializer;
import com.sigpwned.discourse.core.value.deserializer.StringValueDeserializer;

public class SerializationContext {
  private final LinkedList<ValueDeserializer<?>> deserializers;

  public SerializationContext() {
    deserializers = new LinkedList<>();
    addLast(StringValueDeserializer.INSTANCE);
    addLast(LongValueDeserializer.INSTANCE);
    addLast(IntValueDeserializer.INSTANCE);
    addLast(CharValueDeserializer.INSTANCE);
    addLast(ShortValueDeserializer.INSTANCE);
    addLast(ByteValueDeserializer.INSTANCE);
    addLast(DoubleValueDeserializer.INSTANCE);
    addLast(FloatValueDeserializer.INSTANCE);
    addLast(BooleanValueDeserializer.INSTANCE);
    addLast(InstantValueDeserializer.INSTANCE);
    addLast(LocalDateTimeValueDeserializer.INSTANCE);
    addLast(LocalDateValueDeserializer.INSTANCE);
    addLast(LocalTimeValueDeserializer.INSTANCE);
  }

  public Optional<ValueDeserializer<?>> getDeserializer(Type genericType,
      Annotation[] annotations) {
    return deserializers.stream().filter(d -> d.isDeserializable(genericType, annotations))
        .findFirst();
  }

  public void addFirst(ValueDeserializer<?> deserializer) {
    deserializers.remove(deserializer);
    deserializers.addFirst(deserializer);
  }

  public void addLast(ValueDeserializer<?> deserializer) {
    deserializers.remove(deserializer);
    deserializers.addLast(deserializer);
  }

  public List<ValueDeserializer<?>> getDeserializers() {
    return unmodifiableList(deserializers);
  }
}
