package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class BigDecimalValueDeserializerFactory implements ValueDeserializerFactory<BigDecimal> {
  public static final BigDecimalValueDeserializerFactory INSTANCE =
      new BigDecimalValueDeserializerFactory();

  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(BigDecimal.class);
  }

  @Override
  public ValueDeserializer<BigDecimal> getDeserializer(Type genericType, List<Annotation> annotations) {
    return BigDecimal::new;
  }
}
