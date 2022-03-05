package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueDeserializer;

public class BooleanValueDeserializer implements ValueDeserializer<Boolean> {
  public static final BooleanValueDeserializer INSTANCE=new BooleanValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(Boolean.class) || genericType.equals(boolean.class);
  }

  @Override
  public Boolean deserialize(Type genericType, Annotation[] annotations, String value) {
    return Boolean.valueOf(value);
  }
}
