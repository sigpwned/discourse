package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueDeserializer;

public class LongValueDeserializer implements ValueDeserializer<Long> {
  public static final LongValueDeserializer INSTANCE=new LongValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(Long.class) || genericType.equals(long.class);
  }

  @Override
  public Long deserialize(Type genericType, Annotation[] annotations, String value) {
    return Long.valueOf(value);
  }
}
