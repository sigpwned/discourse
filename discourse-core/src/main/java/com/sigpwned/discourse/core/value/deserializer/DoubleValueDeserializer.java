package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueDeserializer;

public class DoubleValueDeserializer implements ValueDeserializer<Double> {
  public static final DoubleValueDeserializer INSTANCE=new DoubleValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(Double.class) || genericType.equals(double.class);
  }

  @Override
  public Double deserialize(Type genericType, Annotation[] annotations, String value) {
    return Double.valueOf(value);
  }
}
