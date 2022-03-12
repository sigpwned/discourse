package com.sigpwned.discourse.guava.serialization;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

/**
 * Returns a ByteSource.
 */
public class ByteSourceValueDeserializerFactory implements ValueDeserializerFactory<ByteSource> {
  public static final ByteSourceValueDeserializerFactory INSTANCE =
      new ByteSourceValueDeserializerFactory();

  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(ByteSource.class);
  }

  @Override
  public ValueDeserializer<ByteSource> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    return s -> {
      try {
        return Resources.asByteSource(new URL(s));
      } catch (MalformedURLException e) {
        return Files.asByteSource(new File(s));
      }
    };
  }
}
