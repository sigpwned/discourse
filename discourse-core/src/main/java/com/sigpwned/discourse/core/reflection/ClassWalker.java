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
package com.sigpwned.discourse.core.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Implements the visitor pattern for walking individual classes and class hierarchies.
 */
public class ClassWalker {

  public static interface Visitor<T> {

    default void beginClass(Class<? super T> clazz) {
    }

    default void constructor(Constructor<? super T> constructor) {
    }

    default void field(Field field) {
    }

    default void method(Method method) {
    }

    default void endClass(Class<? super T> clazz) {
    }
  }

  public <T> void walkClass(Class<T> clazz, Visitor<? extends T> visitor) {
    visitor.beginClass(clazz);
    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      visitor.constructor((Constructor<T>) constructor);
    }
    for (Field field : clazz.getDeclaredFields()) {
      visitor.field(field);
    }
    for (Method method : clazz.getDeclaredMethods()) {
      visitor.method(method);
    }
    visitor.endClass(clazz);
  }

  public <T> void walkClassAndSuperclasses(Class<T> clazz, Visitor<? extends T> visitor) {
    for (Class<? super T> current = clazz; current != null; current = current.getSuperclass()) {
      walkClass(current, visitor);
    }
  }
}