package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

/**
 * Parses a {@link LocalTime} according to ISO-8601, e.g. 10:15.
 * 
 * @see DateTimeFormatter#ISO_LOCAL_TIME
 */
public class LocalTimeValueDeserializerFactory implements ValueDeserializerFactory<LocalTime> {
  public static final LocalTimeValueDeserializerFactory INSTANCE=new LocalTimeValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(LocalTime.class);
  }

  @Override
  public ValueDeserializer<LocalTime> getDeserializer(Type genericType, List<Annotation> annotations) {
    return LocalTime::parse;
  }
}
