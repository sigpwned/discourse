package com.sigpwned.discourse.core.reflection;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

public class FieldInstanceAttributeView extends InstanceAttributeView {

  private final Field field;
  private final Visibility visibility;
  private final boolean mutable;

  public FieldInstanceAttributeView(Field field) {
    this.field = requireNonNull(field);
    if (Modifier.isStatic(field.getModifiers())) {
      throw new IllegalArgumentException("field must be an instance field");
    }
    this.visibility = Visibility.fromModifiers(field.getModifiers());
    this.mutable = !Modifier.isFinal(field.getModifiers());
  }

  @Override
  public Visibility getVisibility() {
    return visibility;
  }

  @Override
  public String getAttributeName() {
    return getField().getName();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getField().getAnnotations());
  }

  public boolean isMutable() {
    return mutable;
  }

  @Override
  public Class<?> getRawType() {
    return getField().getType();
  }

  @Override
  public Type getGenericType() {
    return getField().getGenericType();
  }

  @Override
  public boolean canGet() {
    return getVisibility() == Visibility.PUBLIC;
  }

  @Override
  public Object get(Object instance) {
    try {
      return getField().get(instance);
    } catch (IllegalAccessException e) {
      // TODO Better exception
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean canSet() {
    return getVisibility() == Visibility.PUBLIC && !isMutable();
  }

  @Override
  public void set(Object instance, Object value) {
    try {
      getField().set(instance, value);
    } catch (IllegalAccessException e) {
      // TODO Better exception
      throw new RuntimeException(e);
    }
  }

  private Field getField() {
    return field;
  }
}
