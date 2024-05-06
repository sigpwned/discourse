package com.sigpwned.discourse.core.configurable.component;

import java.util.Comparator;

/**
 * Compares two classes based on their inheritance relationship. Classes must be from the same
 * {@link ClassLoader} and related by inheritance, or else a {@link IllegalArgumentException} is
 * thrown.
 */
public class InheritanceClassComparator implements Comparator<Class<?>> {

  public static final InheritanceClassComparator INSTANCE = new InheritanceClassComparator();

  @Override
  public int compare(Class<?> a, Class<?> b) {
    if (a.getClassLoader() != b.getClassLoader()) {
      throw new IllegalArgumentException("classes must be from the same ClassLoader");
    }
    if (a == b) {
      return 0;
    }
    if (a.isAssignableFrom(b)) {
      return -1;
    }
    if (b.isAssignableFrom(a)) {
      return 1;
    }
    throw new IllegalArgumentException("classes are not related");
  }
}
