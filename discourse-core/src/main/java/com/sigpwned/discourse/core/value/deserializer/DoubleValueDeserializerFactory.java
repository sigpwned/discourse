package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class DoubleValueDeserializerFactory implements ValueDeserializerFactory<Double> {
  public static final DoubleValueDeserializerFactory INSTANCE=new DoubleValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(Double.class) || genericType.equals(double.class);
  }

  @Override
  public ValueDeserializer<Double> getDeserializer(Type genericType, List<Annotation> annotations) {
    return Double::valueOf;
  }
}
