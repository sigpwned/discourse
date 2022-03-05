package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.sigpwned.discourse.core.ValueDeserializer;

/**
 * Parses a {@link LocalDateTime} according to ISO-8601, e.g. 2011-12-03:10:15:30.
 * 
 * @see DateTimeFormatter#ISO_LOCAL_DATE_TIME
 */
public class LocalDateTimeValueDeserializer implements ValueDeserializer<LocalDateTime> {
  public static final LocalDateTimeValueDeserializer INSTANCE=new LocalDateTimeValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(LocalDateTime.class);
  }

  @Override
  public LocalDateTime deserialize(Type genericType, Annotation[] annotations, String value) {
    return LocalDateTime.parse(value);
  }
}
