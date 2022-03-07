package com.sigpwned.discourse.core.util;

import java.lang.reflect.Type;
import java.util.Objects;

public class GenericArrayType {
  public static GenericArrayType parse(Type genericType) {
    if (genericType instanceof Class<?>) {
      Class<?> classType = (Class<?>) genericType;
      if (classType.getComponentType() == null)
        throw new IllegalArgumentException("genericType is not an array type");
      Class<?> elementType=classType.getComponentType();
      if(!Types.isConcrete(elementType))
        throw new IllegalArgumentException("elementType is not concrete");
      return of(elementType);
    } else if (genericType instanceof GenericArrayType) {
      java.lang.reflect.GenericArrayType arrayType = (java.lang.reflect.GenericArrayType) genericType;
      Type elementType=arrayType.getGenericComponentType();
      if(!Types.isConcrete(elementType))
        throw new IllegalArgumentException("elementType is not concrete");
      return of(elementType);
    } else {
      throw new IllegalArgumentException("genericType is not an array type");
    }
  }

  public static GenericArrayType of(Type elementType) {
    return new GenericArrayType(elementType);
  }

  private final Type elementType;

  public GenericArrayType(Type elementType) {
    if (elementType.equals(void.class))
      throw new IllegalArgumentException("elementType cannot be void");
    if (Types.isPrimitive(elementType))
      throw new IllegalArgumentException("elementType cannot be primitive");
    if(!Types.isConcrete(elementType))
      throw new IllegalArgumentException("elementType must be concrete");
    this.elementType = elementType;
  }

  /**
   * @return the elementType
   */
  public Type getElementType() {
    return elementType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(elementType);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GenericArrayType other = (GenericArrayType) obj;
    return Objects.equals(elementType, other.elementType);
  }

  @Override
  public String toString() {
    return "GenericArrayType [elementType=" + elementType + "]";
  }
}
