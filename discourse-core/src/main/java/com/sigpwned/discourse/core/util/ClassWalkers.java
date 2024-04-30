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

import com.sigpwned.discourse.core.reflection.ClassWalker;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public final class ClassWalkers {

  private ClassWalkers() {
  }

  /**
   * Streams the {@link AccessibleObject}s in the given class only.
   *
   * @param clazz the class
   * @param <T>   the type of the class
   * @return a stream of the accessible objects in the given class
   */
  public static <T> Stream<AccessibleObject> streamClass(Class<T> clazz) {
    return streamClass(new ClassWalker(), clazz);
  }

  /**
   * Streams the {@link AccessibleObject}s in the given class only.
   *
   * @param walker the class walker to use
   * @param clazz  the class
   * @param <T>    the type of the class
   * @return a stream of the accessible objects in the given class
   */
  public static <T> Stream<AccessibleObject> streamClass(ClassWalker walker, Class<T> clazz) {
    return stream(walker::walkClass, clazz);
  }

  /**
   * Streams the {@link AccessibleObject}s in the given class and its superclasses.
   *
   * @param clazz the class
   * @param <T>   the type of the class
   * @return a stream of the accessible objects in the given class and its superclasses
   */
  public static <T> Stream<AccessibleObject> streamClassAndSuperclasses(Class<T> clazz) {
    return streamClassAndSuperclasses(new ClassWalker(), clazz);
  }

  /**
   * Streams the {@link AccessibleObject}s in the given class and its superclasses.
   *
   * @param walker the class walker to use
   * @param clazz  the class
   * @param <T>    the type of the class
   * @return a stream of the accessible objects in the given class and its superclasses
   */
  public static <T> Stream<AccessibleObject> streamClassAndSuperclasses(ClassWalker walker,
      Class<T> clazz) {
    return stream(walker::walkClassAndSuperclasses, clazz);
  }

  /**
   * Streams accessible objects using the given walker and root class.
   *
   * @param walker
   * @param clazz
   * @param <T>
   * @param <U>
   * @return
   */
  private static <T, U extends T> Stream<AccessibleObject> stream(
      BiConsumer<Class<T>, ClassWalker.Visitor<U>> walker, Class<T> clazz) {
    final List<AccessibleObject> result = new ArrayList<>();
    walker.accept(clazz, new ClassWalker.Visitor<U>() {
      @Override
      public void constructor(Constructor<? super U> constructor) {
        result.add(constructor);
      }

      @Override
      public void field(Field field) {
        result.add(field);
      }

      @Override
      public void method(Method method) {
        result.add(method);
      }
    });
    return result.stream();
  }
}
