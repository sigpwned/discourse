package com.sigpwned.discourse.core.util;

import static java.lang.String.format;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class GenericListType {
  public static GenericListType parse(Type genericType) {
    if (genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      if (!parameterizedType.getRawType().equals(List.class))
        throw new IllegalArgumentException("Not a List type");
      if (parameterizedType.getActualTypeArguments().length != 1)
        throw new AssertionError(format("List type unexpectedly has %d type arguments",
            parameterizedType.getActualTypeArguments().length));
      Type elementType = parameterizedType.getActualTypeArguments()[0];
      return of(elementType);
    } else {
      throw new IllegalArgumentException("Not a parameterized type");
    }
  }

  public static GenericListType of(Type elementType) {
    return new GenericListType(elementType);
  }

  private final Type elementType;

  public GenericListType(Type elementType) {
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
    GenericListType other = (GenericListType) obj;
    return Objects.equals(elementType, other.elementType);
  }

  @Override
  public String toString() {
    return "GenericListType [elementType=" + elementType + "]";
  }
}
