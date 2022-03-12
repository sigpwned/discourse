package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class CharValueDeserializerFactory implements ValueDeserializerFactory<Character> {
  public static final CharValueDeserializerFactory INSTANCE=new CharValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(Character.class) || genericType.equals(char.class);
  }

  @Override
  public ValueDeserializer<Character> getDeserializer(Type genericType, List<Annotation> annotations) {
    return value -> {
      if (value.length() == 0)
        throw new IllegalArgumentException("no character");
      if (value.length() != 1)
        throw new IllegalArgumentException("too many characters");
      return Character.valueOf(value.charAt(0));
    };
  }
}
