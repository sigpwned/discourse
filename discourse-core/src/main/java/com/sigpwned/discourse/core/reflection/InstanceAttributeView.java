package com.sigpwned.discourse.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public abstract class InstanceAttributeView implements ReadingInstanceAttributeView,
    WritingInstanceAttributeView {

  public abstract Visibility getVisibility();

  public abstract String getAttributeName();

  public abstract Class<?> getRawType();

  public abstract Type getGenericType();

  public abstract List<Annotation> getAnnotations();

  public boolean canGet() {
    return false;
  }

  @Override
  public Object get(Object instance) {
    throw new UnsupportedOperationException();
  }

  public boolean canSet() {
    return false;
  }

  @Override
  public void set(Object instance, Object value) {
    throw new UnsupportedOperationException();
  }
}
