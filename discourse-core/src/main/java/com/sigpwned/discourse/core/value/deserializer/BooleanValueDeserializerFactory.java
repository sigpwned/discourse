package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class BooleanValueDeserializerFactory implements ValueDeserializerFactory<Boolean> {
  public static final BooleanValueDeserializerFactory INSTANCE=new BooleanValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(Boolean.class) || genericType.equals(boolean.class);
  }

  @Override
  public ValueDeserializer<Boolean> getDeserializer(Type genericType, List<Annotation> annotations) {
    return Boolean::valueOf;
  }
}
