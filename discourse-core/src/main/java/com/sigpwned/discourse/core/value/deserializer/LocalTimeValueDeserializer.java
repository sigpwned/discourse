package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import com.sigpwned.discourse.core.ValueDeserializer;

/**
 * Parses a {@link LocalTime} according to ISO-8601, e.g. 10:15.
 * 
 * @see DateTimeFormatter#ISO_LOCAL_TIME
 */
public class LocalTimeValueDeserializer implements ValueDeserializer<LocalTime> {
  public static final LocalTimeValueDeserializer INSTANCE=new LocalTimeValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(LocalTime.class);
  }

  @Override
  public LocalTime deserialize(Type genericType, Annotation[] annotations, String value) {
    return LocalTime.parse(value);
  }
}
