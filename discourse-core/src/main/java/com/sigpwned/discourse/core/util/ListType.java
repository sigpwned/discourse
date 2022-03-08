package com.sigpwned.discourse.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class ListType {
  public static ListType parse(Type genericType) {
    if (genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      if (!parameterizedType.getRawType().equals(List.class))
        throw new IllegalArgumentException("Not a List type");
      Type elementType = parameterizedType.getActualTypeArguments()[0];
      return of(elementType);
    } else {
      throw new IllegalArgumentException("Not a parameterized type");
    }
  }

  public static ListType of(Type elementType) {
    return new ListType(elementType);
  }

  private final Type elementType;

  public ListType(Type elementType) {
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
    ListType other = (ListType) obj;
    return Objects.equals(elementType, other.elementType);
  }

  @Override
  @Generated
  public String toString() {
    return "GenericListType [elementType=" + elementType + "]";
  }
}
