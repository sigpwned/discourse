package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class LongValueDeserializerFactory implements ValueDeserializerFactory<Long> {
  public static final LongValueDeserializerFactory INSTANCE=new LongValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(Long.class) || genericType.equals(long.class);
  }

  @Override
  public ValueDeserializer<Long> getDeserializer(Type genericType, List<Annotation> annotations) {
    return Long::valueOf;
  }
}
