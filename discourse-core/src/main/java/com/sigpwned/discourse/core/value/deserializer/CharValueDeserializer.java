package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueDeserializer;

public class CharValueDeserializer implements ValueDeserializer<Character> {
  public static final CharValueDeserializer INSTANCE=new CharValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(Character.class) || genericType.equals(char.class);
  }

  @Override
  public Character deserialize(Type genericType, Annotation[] annotations, String value) {
    if (value.length() == 0)
      throw new IllegalArgumentException("no character");
    if (value.length() != 1)
      throw new IllegalArgumentException("too many characters");
    return Character.valueOf(value.charAt(0));
  }
}
