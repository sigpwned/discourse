package com.sigpwned.discourse.core.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

public class ArrayType {
  public static ArrayType parse(Type genericType) {
    if (genericType instanceof Class<?>) {
      Class<?> classType = (Class<?>) genericType;
      if (classType.getComponentType() == null)
        throw new IllegalArgumentException("genericType is not an array type");
      Class<?> elementType=classType.getComponentType();
      if(!Types.isConcrete(elementType))
        throw new IllegalArgumentException("elementType is not concrete");
      return of(elementType);
    } else if (genericType instanceof GenericArrayType) {
      GenericArrayType arrayType = (GenericArrayType) genericType;
      Type elementType=arrayType.getGenericComponentType();
      if(!Types.isConcrete(elementType))
        throw new IllegalArgumentException("elementType is not concrete");
      return of(elementType);
    } else {
      throw new IllegalArgumentException("genericType is not an array type");
    }
  }

  public static ArrayType of(Type elementType) {
    return new ArrayType(elementType);
  }

  private final Type elementType;

  public ArrayType(Type elementType) {
    if (elementType.equals(void.class))
      throw new IllegalArgumentException("elementType cannot be void");
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
  @Generated
  public int hashCode() {
    return Objects.hash(elementType);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ArrayType other = (ArrayType) obj;
    return Objects.equals(elementType, other.elementType);
  }

  @Override
  @Generated
  public String toString() {
    return "GenericArrayType [elementType=" + elementType + "]";
  }
}
