package com.sigpwned.discourse.core.reflection;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;

/**
 * @param type The type of the field
 * @param name The name of the field
 */
public record FieldSignature(Class<?> type, String name) {

  public static FieldSignature fromField(Field field) {
    return new FieldSignature(field.getType(), field.getName());
  }

  public FieldSignature {
    type = requireNonNull(type);
    name = requireNonNull(name);
  }

  public boolean isAssignableFrom(FieldSignature that) {
    return this.name().equals(that.name()) && this.type().isAssignableFrom(that.type());
  }
}
