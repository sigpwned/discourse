package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.reflection.ClassWalker;
import com.sigpwned.discourse.core.reflection.DefaultClassWalker;
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

  public static <T> Stream<AccessibleObject> streamClass(Class<T> clazz) {
    return streamClass(new DefaultClassWalker(), clazz);
  }

  public static <T> Stream<AccessibleObject> streamClass(ClassWalker walker, Class<T> clazz) {
    return stream(walker::walkClass, clazz);
  }

  public static <T> Stream<AccessibleObject> streamClassAndSuperclasses(Class<T> clazz) {
    return streamClassAndSuperclasses(new DefaultClassWalker(), clazz);
  }

  public static <T> Stream<AccessibleObject> streamClassAndSuperclasses(ClassWalker walker,
      Class<T> clazz) {
    return stream(walker::walkClassAndSuperclasses, clazz);
  }

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
