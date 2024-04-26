package com.sigpwned.discourse.core.reflection;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Type;

public abstract class Attribute {

  private final String name;
  private final Class<?> rawType;
  private final Type genericType;

  public Attribute(String name, Class<?> rawType, Type genericType) {
    this.name = requireNonNull(name);
    this.rawType = requireNonNull(rawType);
    this.genericType = requireNonNull(genericType);
  }

  public String getName() {
    return name;
  }

  public Class<?> getRawType() {
    return rawType;
  }

  public Type getGenericType() {
    return genericType;
  }


}
