package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class ShortValueDeserializerFactory implements ValueDeserializerFactory<Short> {
  public static final ShortValueDeserializerFactory INSTANCE=new ShortValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(Short.class) || genericType.equals(short.class);
  }

  @Override
  public ValueDeserializer<Short> getDeserializer(Type genericType, List<Annotation> annotations) {
    return Short::valueOf;
  }
}
