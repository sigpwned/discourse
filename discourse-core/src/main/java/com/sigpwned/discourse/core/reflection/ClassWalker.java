package com.sigpwned.discourse.core.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ClassWalker {

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

  <T> void walkClass(Class<T> clazz, Visitor<? extends T> visitor);

  <T> void walkClassAndSuperclasses(Class<T> clazz, Visitor<? extends T> visitor);
}
