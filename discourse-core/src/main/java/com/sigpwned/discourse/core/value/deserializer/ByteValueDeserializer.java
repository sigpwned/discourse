package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueDeserializer;

public class ByteValueDeserializer implements ValueDeserializer<Byte> {
  public static final ByteValueDeserializer INSTANCE=new ByteValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(Byte.class) || genericType.equals(byte.class);
  }

  @Override
  public Byte deserialize(Type genericType, Annotation[] annotations, String value) {
    return Byte.valueOf(value);
  }
}
