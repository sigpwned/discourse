/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class Reflection {

  private Reflection() {}

  /**
   * <p>
   * Returns {@code true} if the given method is a factory method. That is:
   * </p>
   *
   * <ul>
   * <li>It returns something other than {@code void}</li>
   * <li>It is static</li>
   * </ul>
   *
   * <p>
   * This method does not check the name of the method or its visibility.
   * </p>
   *
   * @param method the method to check
   * @return {@code true} if the given method is a factory method
   */
  public static boolean hasFactoryMethodSignature(Method method) {
    return !void.class.equals(method.getReturnType()) && Modifier.isStatic(method.getModifiers());
  }

  /**
   * <p>
   * Returns {@code true} if the given field is a mutable instance field. That is:
   * </p>
   *
   * <ul>
   * <li>It is not static</li>
   * <li>It is not final</li>
   * </ul>
   *
   * <p>
   * This method does not check the name of the field or its visibility.
   * </p>
   *
   * @param field the field to check
   * @return {@code true} if the given field is an instance field that is mutable
   */
  public static boolean isMutableInstanceField(Field field) {
    return !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers());
  }

  /**
   * <p>
   * Returns {@code true} if the given method has the signature of a default constructor. That is:
   * </p>
   * 
   * <ul>
   * <li>It has no parameters</li>
   * </ul>
   * 
   * <p>
   * This method does not check the visibility of the constructor.
   * </p>
   * 
   * @param constructor the constructor to check
   * @return {@code true} if the given constructor has the signature of a default constructor,
   *         {@code false} otherwise
   */
  public static boolean hasDefaultConstructorSignature(Constructor<?> constructor) {
    return constructor.getParameterCount() == 0;
  }

  /**
   * <p>
   * Returns {@code true} if the given method has the signature of a getter method. That is:
   * </p>
   *
   * <ul>
   * <li>It has no parameters</li>
   * <li>It returns something other than {@code void}</li>
   * <li>It is not static</li>
   * </ul>
   *
   * <p>
   * This method does not check the name of the method or its visibility.
   * </p>
   *
   * @param method the method to check
   * @return {@code true} if the given method has the signature of a getter method
   */
  public static boolean hasInstanceGetterSignature(Method method) {
    return method.getParameterCount() == 0 && !void.class.equals(method.getReturnType())
        && !Modifier.isStatic(method.getModifiers());
  }

  /**
   * <p>
   * Returns {@code true} if the given method has the signature of a setter method. That is:
   * </p>
   *
   * <ul>
   * <li>It takes a single parameter</li>
   * <li>It returns {@code void}</li>
   * <li>It is not static</li>
   * </ul>
   *
   * <p>
   * This method does not check the name of the method or its visibility.
   * </p>
   *
   * @param method the method to check
   * @return {@code true} if the given method has the signature of a setter method
   */
  public static boolean hasInstanceSetterSignature(Method method) {
    return method.getParameterCount() == 1 && void.class.equals(method.getReturnType())
        && !Modifier.isStatic(method.getModifiers());
  }
}
