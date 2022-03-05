package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.sigpwned.discourse.core.ValueDeserializer;

/**
 * Parses a {@link LocalDate} according to ISO-8601, e.g. 2007-12-03.
 * 
 * @see DateTimeFormatter#ISO_LOCAL_DATE
 */
public class LocalDateValueDeserializer implements ValueDeserializer<LocalDate> {
  public static final LocalDateValueDeserializer INSTANCE=new LocalDateValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(LocalDate.class);
  }

  @Override
  public LocalDate deserialize(Type genericType, Annotation[] annotations, String value) {
    return LocalDate.parse(value);
  }
}
