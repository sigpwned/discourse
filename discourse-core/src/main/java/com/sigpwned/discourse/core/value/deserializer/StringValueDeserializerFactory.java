package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class StringValueDeserializerFactory implements ValueDeserializerFactory<String> {
  public static final StringValueDeserializerFactory INSTANCE=new StringValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(String.class);
  }

  @Override
  public ValueDeserializer<String> getDeserializer(Type genericType, List<Annotation> annotations) {
    return x -> x;
  }
}
