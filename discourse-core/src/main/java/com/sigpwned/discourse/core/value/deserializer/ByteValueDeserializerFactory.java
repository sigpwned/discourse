package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class ByteValueDeserializerFactory implements ValueDeserializerFactory<Byte> {
  public static final ByteValueDeserializerFactory INSTANCE=new ByteValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(Byte.class) || genericType.equals(byte.class);
  }

  @Override
  public ValueDeserializer<Byte> getDeserializer(Type genericType, List<Annotation> annotations) {
    return Byte::valueOf;
  }
}
