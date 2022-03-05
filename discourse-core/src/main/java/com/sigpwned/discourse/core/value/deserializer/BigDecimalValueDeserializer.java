package com.sigpwned.discourse.core.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import com.sigpwned.discourse.core.ValueDeserializer;

public class BigDecimalValueDeserializer implements ValueDeserializer<BigDecimal> {
  public static final BigDecimalValueDeserializer INSTANCE=new BigDecimalValueDeserializer();
  
  @Override
  public boolean isDeserializable(Type genericType, Annotation[] annotations) {
    return genericType.equals(BigDecimal.class);
  }

  @Override
  public BigDecimal deserialize(Type genericType, Annotation[] annotations, String value) {
    return new BigDecimal(value);
  }
}
