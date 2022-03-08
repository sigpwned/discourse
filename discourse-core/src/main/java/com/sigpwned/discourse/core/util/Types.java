package com.sigpwned.discourse.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public final class Types {
  private Types() {}

  public static boolean isPrimitive(Type genericType) {
    return genericType.equals(byte.class) || genericType.equals(short.class)
        || genericType.equals(int.class) || genericType.equals(long.class)
        || genericType.equals(float.class) || genericType.equals(double.class)
        || genericType.equals(char.class) || genericType.equals(boolean.class);
  }

  /**
   * Returns a new instance of the given fully-resolved array type.
   * 
   * @throws IllegalArgumentException if the given type is not a fully-resolved array type
   * @see #isConcreteArrayType(Type)
   */
  public static Object newConcreteArrayInstance(Type genericType, int length) {
    Class<?> classType = JodaBeanUtils.eraseToClass(genericType);
    if (classType.getComponentType() == null)
      throw new IllegalArgumentException("not an array type");
    return Array.newInstance(classType.getComponentType(), length);
  }

  /**
   * Returns true if the given type is fully resolved. A fully resolved type is either (a) a
   * {@link Class} instance; (b) a {@link ParameterizedType} with fully resolved type arguments; or
   * (c) a {@link GenericArrayType} with a fully resolved component type. Note that if a type is
   * concrete, then its parent and interfaces must be, too.
   */
  public static boolean isConcrete(Type genericType) {
    if (genericType instanceof Class<?>)
      return true;
    if (genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      return Arrays.stream(parameterizedType.getActualTypeArguments()).allMatch(Types::isConcrete);
    }
    if (genericType instanceof GenericArrayType) {
      GenericArrayType arrayType = (GenericArrayType) genericType;
      return isConcrete(arrayType.getGenericComponentType());
    }
    return false;
  }
}
