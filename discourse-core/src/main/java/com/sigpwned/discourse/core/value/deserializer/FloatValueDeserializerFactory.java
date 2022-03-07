package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class FloatValueDeserializerFactory implements ValueDeserializerFactory<Float> {
  public static final FloatValueDeserializerFactory INSTANCE=new FloatValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(Float.class) || genericType.equals(float.class);
  }

  @Override
  public ValueDeserializer<Float> getDeserializer(Type genericType, List<Annotation> annotations) {
    return Float::valueOf;
  }
}
