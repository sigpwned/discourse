package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueDeserializer;

public class ShortValueDeserializer implements ValueDeserializer<Short> {
  public static final ShortValueDeserializer INSTANCE=new ShortValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(Short.class) || genericType.equals(short.class);
  }

  @Override
  public Short deserialize(Type genericType, Annotation[] annotations, String value) {
    return Short.valueOf(value);
  }
}
