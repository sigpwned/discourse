package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class IntValueDeserializerFactory implements ValueDeserializerFactory<Integer> {
  public static final IntValueDeserializerFactory INSTANCE=new IntValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(Integer.class) || genericType.equals(int.class);
  }

  @Override
  public ValueDeserializer<Integer> getDeserializer(Type genericType, List<Annotation> annotations) {
    return Integer::valueOf;
  }
}
