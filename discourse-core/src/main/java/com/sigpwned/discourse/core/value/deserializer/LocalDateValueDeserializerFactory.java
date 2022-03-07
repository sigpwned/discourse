package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

/**
 * Parses a {@link LocalDate} according to ISO-8601, e.g. 2007-12-03.
 * 
 * @see DateTimeFormatter#ISO_LOCAL_DATE
 */
public class LocalDateValueDeserializerFactory implements ValueDeserializerFactory<LocalDate> {
  public static final LocalDateValueDeserializerFactory INSTANCE=new LocalDateValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(LocalDate.class);
  }

  @Override
  public ValueDeserializer<LocalDate> getDeserializer(Type genericType, List<Annotation> annotations) {
    return LocalDate::parse;
  }
}
