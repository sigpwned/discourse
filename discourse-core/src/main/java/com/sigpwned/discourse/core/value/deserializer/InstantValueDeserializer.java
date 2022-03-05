package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import com.sigpwned.discourse.core.ValueDeserializer;

/**
 * Parses a {@link Instant} according to ISO-8601, e.g. 2011-12-03T10:15:30Z.
 * 
 * @see DateTimeFormatter#ISO_INSTANT
 */
public class InstantValueDeserializer implements ValueDeserializer<Instant> {
  public static final InstantValueDeserializer INSTANCE=new InstantValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(Instant.class);
  }

  @Override
  public Instant deserialize(Type genericType, Annotation[] annotations, String value) {
    return Instant.parse(value);
  }
}
