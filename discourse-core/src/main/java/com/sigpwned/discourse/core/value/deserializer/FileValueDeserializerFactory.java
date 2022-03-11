package com.sigpwned.discourse.core.value.deserializer;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class FileValueDeserializerFactory implements ValueDeserializerFactory<File> {
  public static final FileValueDeserializerFactory INSTANCE=new FileValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(File.class);
  }

  @Override
  public ValueDeserializer<File> getDeserializer(Type genericType, List<Annotation> annotations) {
    return File::new;
  }
}