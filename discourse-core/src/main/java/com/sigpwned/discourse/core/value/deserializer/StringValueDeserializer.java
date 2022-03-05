package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueDeserializer;

public class StringValueDeserializer implements ValueDeserializer<String> {
  public static final StringValueDeserializer INSTANCE=new StringValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(String.class);
  }

  @Override
  public String deserialize(Type genericType, Annotation[] annotations, String value) {
    return value;
  }
}
