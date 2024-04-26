package com.sigpwned.discourse.core.reflection;

import com.sigpwned.discourse.core.util.ParameterAnnotations;
import com.sigpwned.discourse.core.util.Streams;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class InstanceAttribute {

  public static InstanceAttribute fromViews(FieldInstanceAttributeView field,
      GetterInstanceAttributeView getter, SetterInstanceAttributeView setter) {
    if (field == null && getter == null && setter == null) {
      throw new IllegalArgumentException(
          "at least one of field, getter, or setter must not be null");
    }

    if (Stream.of(field, getter, setter).filter(Objects::nonNull)
        .map(InstanceAttributeView::getAttributeName).distinct().count() > 1) {
      throw new IllegalArgumentException("attribute names do not match");
    }

    Class<?> rawType;
    if(field != null) {
      rawType = field.getRawType();
    } else if(getter != null) {
      rawType = getter.getRawType();
    } else {
      rawType = setter.getRawType();
    }


    List<Annotation> parameterAnnotations = Streams.concat(
            Optional.ofNullable(field).map(f -> f.getAnnotations().stream()).orElseGet(Stream::empty),
            Optional.ofNullable(getter).map(g -> g.getAnnotations().stream()).orElseGet(Stream::empty),
            Optional.ofNullable(setter).map(s -> s.getAnnotations().stream()).orElseGet(Stream::empty))
        .filter(ParameterAnnotations::isParameterAnnotation).toList();
    if (parameterAnnotations.size() > 1) {
      throw new IllegalArgumentException("multiple parameter annotations");
    }

    if (field != null) {
      if (field.canGet() && field.canSet()) {
      } else {

      }
    } else {

    }
  }

  private final String attributeName;
  private final Class<?> rawType;
  private final Type genericType;
  private final ReadingInstanceAttributeView reader;
  private final WritingInstanceAttributeView writer;

  public InstanceAttribute(FieldInstanceAttributeView field, GetterInstanceAttributeView getter,
      SetterInstanceAttributeView setter) {
    if (field != null) {
      if (field.canGet() && field.canSet()) {

      } else {

      }
    } else {

    }
  }

  public String getAttributeName() {
    return attributeName;
  }

  public Class<?> getRawType() {
    return rawType;
  }

  public Type getGenericType() {
    return genericType;
  }

  public Optional<ReadingInstanceAttributeView> getReader() {
    return Optional.ofNullable(reader);
  }

  public Optional<WritingInstanceAttributeView> getWriter() {
    return Optional.ofNullable(writer);
  }
}
