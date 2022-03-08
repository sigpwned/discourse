package com.sigpwned.discourse.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.SortedSet;

public class SortedSetType {
  public static SortedSetType parse(Type genericType) {
    if (genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      if (!parameterizedType.getRawType().equals(SortedSet.class))
        throw new IllegalArgumentException("Not a SortedSet type");
      Type elementType = parameterizedType.getActualTypeArguments()[0];
      return of(elementType);
    } else {
      throw new IllegalArgumentException("Not a parameterized type");
    }
  }

  public static SortedSetType of(Type elementType) {
    return new SortedSetType(elementType);
  }

  private final Type elementType;

  public SortedSetType(Type elementType) {
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
    SortedSetType other = (SortedSetType) obj;
    return Objects.equals(elementType, other.elementType);
  }

  @Override
  @Generated
  public String toString() {
    return "GenericSortedSetType [elementType=" + elementType + "]";
  }
}
