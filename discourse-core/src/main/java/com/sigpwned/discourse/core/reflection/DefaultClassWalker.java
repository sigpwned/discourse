package com.sigpwned.discourse.core.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultClassWalker implements ClassWalker {

  @Override
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

  @Override
  public <T> void walkClassAndSuperclasses(Class<T> clazz, Visitor<? extends T> visitor) {
    for (Class<? super T> current = clazz; current != null; current = current.getSuperclass()) {
      walkClass(current, visitor);
    }
  }
}
