package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueDeserializer;

public class FloatValueDeserializer implements ValueDeserializer<Float> {
  public static final FloatValueDeserializer INSTANCE=new FloatValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(Float.class) || genericType.equals(float.class);
  }

  @Override
  public Float deserialize(Type genericType, Annotation[] annotations, String value) {
    return Float.valueOf(value);
  }
}
