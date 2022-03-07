package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

/**
 * Parses a {@link Instant} according to ISO-8601, e.g. 2011-12-03T10:15:30Z.
 * 
 * @see DateTimeFormatter#ISO_INSTANT
 */
public class InstantValueDeserializerFactory implements ValueDeserializerFactory<Instant> {
  public static final InstantValueDeserializerFactory INSTANCE=new InstantValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(Instant.class);
  }

  @Override
  public ValueDeserializer<Instant> getDeserializer(Type genericType, List<Annotation> annotations) {
    return Instant::parse;
  }
}
