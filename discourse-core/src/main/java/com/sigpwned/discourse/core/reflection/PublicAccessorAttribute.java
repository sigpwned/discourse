package com.sigpwned.discourse.core.reflection;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.AccessorNamingScheme;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class PublicAccessorAttribute extends Attribute {

  public static PublicAccessorAttribute of(Field field, Method getter, Method setter) {
    return new PublicAccessorAttribute(field.getName(), field.getType(), field.getGenericType(),
        field, getter, setter);
  }

  public static PublicAccessorAttribute of(AccessorNamingScheme naming, Field field, Method getter,
      Method setter) {
    if (getter == null && setter == null) {
      throw new IllegalArgumentException("at least one of getter or setter must not be null");
    }

    if (field != null) {
      // We'll take our name, rawType, and genericType from the field
      if (getter != null && !naming.computeGetterName(field.getName()).equals(getter.getName())) {
        throw new IllegalArgumentException("getter method name does not match field name");
      }
      if (setter != null && !naming.computeSetterName(field.getName()).equals(setter.getName())) {
        throw new IllegalArgumentException("getter method return type does not match field type");
      }
    } else {

    }

    return new PublicAccessorAttribute(field.getName(), field.getType(), field.getGenericType(),
        field, getter, setter);
  }

  private final Field field;
  private final Method getter;
  private final Method setter;

  private PublicAccessorAttribute(String name, Class<?> rawType, Type genericType, Field field,
      Method getter, Method setter) {
    super(name, rawType, genericType);
    this.field = requireNonNull(field);
  }

  public Field getField() {
    return field;
  }
}
