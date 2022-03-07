package com.sigpwned.discourse.core.util;

import static java.lang.String.format;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

public class GenericSetType {
  public static GenericSetType parse(Type genericType) {
    if (genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      if (!parameterizedType.getRawType().equals(Set.class))
        throw new IllegalArgumentException("Not a Set type");
      if (parameterizedType.getActualTypeArguments().length != 1)
        throw new AssertionError(format("Set type unexpectedly has %d type arguments",
            parameterizedType.getActualTypeArguments().length));
      Type elementType = parameterizedType.getActualTypeArguments()[0];
      return of(elementType);
    } else {
      throw new IllegalArgumentException("Not a parameterized type");
    }
  }

  public static GenericSetType of(Type elementType) {
    return new GenericSetType(elementType);
  }

  private final Type elementType;

  public GenericSetType(Type elementType) {
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
    GenericSetType other = (GenericSetType) obj;
    return Objects.equals(elementType, other.elementType);
  }

  @Override
  public String toString() {
    return "GenericSetType [elementType=" + elementType + "]";
  }
}
