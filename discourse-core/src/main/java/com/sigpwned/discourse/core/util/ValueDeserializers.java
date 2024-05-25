package com.sigpwned.discourse.core.util;

import java.util.function.Function;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializer;

public final class ValueDeserializers {
  private ValueDeserializers() {}

  public static <T> Function<String, T> toMapper(ValueDeserializer<T> deserializer) {
    return deserializer::deserialize;
  }
}
