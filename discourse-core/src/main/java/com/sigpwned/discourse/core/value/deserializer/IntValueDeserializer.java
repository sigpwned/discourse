package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueDeserializer;

public class IntValueDeserializer implements ValueDeserializer<Integer> {
  public static final IntValueDeserializer INSTANCE=new IntValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(Integer.class) || genericType.equals(int.class);
  }

  @Override
  public Integer deserialize(Type genericType, Annotation[] annotations, String value) {
    return Integer.valueOf(value);
  }
}
