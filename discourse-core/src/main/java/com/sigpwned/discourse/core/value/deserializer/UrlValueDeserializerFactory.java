package com.sigpwned.discourse.core.value.deserializer;

import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class UrlValueDeserializerFactory implements ValueDeserializerFactory<URL> {
  public static final UrlValueDeserializerFactory INSTANCE=new UrlValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(URL.class);
  }

  @Override
  public ValueDeserializer<URL> getDeserializer(Type genericType, List<Annotation> annotations) {
    return s -> {
      try {
        return new URL(s);
      }
      catch(MalformedURLException e) {
        throw new UncheckedIOException(e);
      }
    };
  }
}
