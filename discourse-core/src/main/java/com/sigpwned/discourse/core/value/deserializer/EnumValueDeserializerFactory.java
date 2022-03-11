package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class EnumValueDeserializerFactory implements ValueDeserializerFactory<Enum<?>> {
  public static final EnumValueDeserializerFactory INSTANCE=new EnumValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    if(genericType instanceof Class<?>) {
      Class<?> classType = (Class<?>) genericType;
      return classType.getEnumConstants() != null;
    }
    return false;
  }

  @Override
  public ValueDeserializer<Enum<?>> getDeserializer(Type genericType, List<Annotation> annotations) {
    Class<?> classType = (Class<?>) genericType;
    return s -> Arrays.stream(classType.getEnumConstants())
        .map(o -> (Enum<?>) o)
        .filter(e -> e.name().equals(s))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No such enum value: "+s));
  }
}