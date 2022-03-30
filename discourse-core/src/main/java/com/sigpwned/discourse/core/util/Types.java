/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
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
  
  public static Class<?> boxed(Class<?> primitiveType) {
    if(primitiveType.equals(boolean.class))
      return Boolean.class;
    if(primitiveType.equals(byte.class))
      return Byte.class;
    if(primitiveType.equals(short.class))
      return Short.class;
    if(primitiveType.equals(int.class))
      return Integer.class;
    if(primitiveType.equals(long.class))
      return Long.class;
    if(primitiveType.equals(float.class))
      return Float.class;
    if(primitiveType.equals(double.class))
      return Double.class;
    if(primitiveType.equals(char.class))
      return Character.class;
    throw new IllegalArgumentException("not a primitive type");
  }

  /**
   * Returns a new instance of the given fully-resolved array type.
   * 
   * @throws IllegalArgumentException if the given type is not a fully-resolved array type
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
