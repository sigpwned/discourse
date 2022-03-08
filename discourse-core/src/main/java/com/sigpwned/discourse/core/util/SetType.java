package com.sigpwned.discourse.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

public class SetType {
  public static SetType parse(Type genericType) {
    if (genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      if (!parameterizedType.getRawType().equals(Set.class))
        throw new IllegalArgumentException("Not a Set type");
      Type elementType = parameterizedType.getActualTypeArguments()[0];
      return of(elementType);
    } else {
      throw new IllegalArgumentException("Not a parameterized type");
    }
  }

  public static SetType of(Type elementType) {
    return new SetType(elementType);
  }

  private final Type elementType;

  public SetType(Type elementType) {
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
    SetType other = (SetType) obj;
    return Objects.equals(elementType, other.elementType);
  }

  @Override
  @Generated
  public String toString() {
    return "GenericSetType [elementType=" + elementType + "]";
  }
}
