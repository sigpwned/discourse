package com.sigpwned.discourse.core.reflection;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;

public class PublicFieldAttribute extends Attribute {

  private final Field field;

  public PublicFieldAttribute(Field field) {
    super(field.getName(), field.getType(), field.getGenericType());
    this.field = requireNonNull(field);
  }

  public Field getField() {
    return field;
  }
}
