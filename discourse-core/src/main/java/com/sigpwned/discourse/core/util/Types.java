package com.sigpwned.discourse.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class Types {
  private Types() {}
  
  /**
   * Returns true if the given type is a fully-resolved array type.
   * 
   * @see #isConcrete(Type)
   */
  public static boolean isConcreteArrayType(Type genericType) {
    if (genericType instanceof Class<?>) {
      Class<?> classType = (Class<?>) genericType;
      if (classType.getComponentType() == null)
        return false;
      return isConcrete(classType.getComponentType());
    } else if (genericType instanceof GenericArrayType) {
      GenericArrayType arrayType = (GenericArrayType) genericType;
      return isConcrete(arrayType.getGenericComponentType());
    } else {
      return false;
    }
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
   * Returns true if the given type is a fully-resolved generic {@link List} type.
   * 
   * @see #isConcrete(Type)
   */
  public static boolean isConcreteListType(Type genericType) {
    return isConcrete(genericType) && implementsInterface(genericType, List.class);
  }

  /**
   * Returns true if the given type is a fully-resolved generic {@link List} type.
   * 
   * @see #isConcrete(Type)
   */
  public static boolean isConcreteSetType(Type genericType) {
    return isConcrete(genericType) && implementsInterface(genericType, Set.class);
  }

  /**
   * Returns true if the given type is a fully-resolved generic {@link List} type.
   * 
   * @see #isConcrete(Type)
   */
  public static boolean isConcreteSortedSetType(Type genericType) {
    return isConcrete(genericType) && implementsInterface(genericType, Set.class);
  }

  /**
   * Returns true if the given type is a fully-resolved generic {@link Collection} type.
   * 
   * @see #isConcrete(Type)
   */
  public static boolean isConcreteCollectionType(Type genericType) {
    return isConcrete(genericType) && implementsInterface(genericType, Collection.class);
  }

  public static final TypeVariable<?> COLLECTION_ELEMENT_TYPE_VARIABLE =
      Collection.class.getTypeParameters()[0];

//  public static Optional<Type> getConcreteCollectionElementType(Type genericType) {
//    return resolveTypeVariable(genericType, COLLECTION_ELEMENT_TYPE_VARIABLE);
//  }

  public static boolean implementsInterface(Type genericType, Class<?> iface) {
    if (genericType instanceof Class<?>) {
      Class<?> type = (Class<?>) genericType;
      if (type.equals(iface))
        return true;
      if (Arrays.stream(type.getGenericInterfaces()).anyMatch(gi -> implementsInterface(gi, iface)))
        return true;
      if (type.getGenericSuperclass() != null)
        return implementsInterface(type.getGenericSuperclass(), iface);
      return false;
    } else if (genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      return implementsInterface(parameterizedType.getRawType(), iface);
    } else if (genericType instanceof GenericArrayType) {
      // array types do not implement interfaces
      return false;
    } else {
      // this type is not fully resolved
      return false;
    }
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

//  /**
//   * Inspired by joda beans
//   * 
//   * @see https://github.com/JodaOrg/joda-beans/blob/72bb2614194a88807cb3e2dd5ea02a47a2f01026/src/main/java/org/joda/beans/JodaBeanUtils.java#L811
//   */
//  public static Optional<Type> resolveTypeVariable(Type genericType, TypeVariable<?> typevar) {
//    Map<Type, Type> resolved = new HashMap<>(getTypeVariableAssignments(genericType));
//
//    Type result = typevar;
//    while (resolved.containsKey(result)) {
//      result = resolved.get(result);
//    }
//
//    return Optional.ofNullable(result).filter(Types::isConcrete);
//  }

//  /**
//   * Inspired by joda beans.
//   * 
//   * @see https://github.com/JodaOrg/joda-beans/blob/72bb2614194a88807cb3e2dd5ea02a47a2f01026/src/main/java/org/joda/beans/JodaBeanUtils.java#L811
//   */
//  private static Map<TypeVariable<?>, Type> getTypeVariableAssignments(Type genericType) {
//    Map<TypeVariable<?>, Type> result = new HashMap<>();
//    while (genericType != null) {
//      if (genericType instanceof Class) {
//        Class<?> classType = (Class<?>) genericType;
//        for (Type interfaceType : classType.getGenericInterfaces())
//          result.putAll(getTypeVariableAssignments(interfaceType));
//        genericType = ((Class<?>) genericType).getGenericSuperclass();
//      } else if (genericType instanceof ParameterizedType) {
//        ParameterizedType parameterizedType = (ParameterizedType) genericType;
//        Type[] arguments = parameterizedType.getActualTypeArguments();
//        Class<?> erasedType = JodaBeanUtils.eraseToClass(parameterizedType.getRawType());
//        if (erasedType == null)
//          return emptyMap();
//        TypeVariable<?>[] typeParameters = erasedType.getTypeParameters();
//        for (int i = 0; i < arguments.length; i++)
//          result.put(typeParameters[i], arguments[i]);
//        for (Type interfaceType : erasedType.getGenericInterfaces())
//          result.putAll(getTypeVariableAssignments(interfaceType));
//        genericType = erasedType.getGenericSuperclass();
//      }
//    }
//    return result;
//  }
}
