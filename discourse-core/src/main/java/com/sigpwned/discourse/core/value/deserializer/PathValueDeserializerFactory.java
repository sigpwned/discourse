package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class PathValueDeserializerFactory implements ValueDeserializerFactory<Path> {
  public static final PathValueDeserializerFactory INSTANCE=new PathValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(Path.class);
  }

  @Override
  public ValueDeserializer<Path> getDeserializer(Type genericType, List<Annotation> annotations) {
    return Path::of;
  }
}