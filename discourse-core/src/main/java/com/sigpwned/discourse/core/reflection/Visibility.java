package com.sigpwned.discourse.core.reflection;

public enum Visibility {
  PUBLIC, PROTECTED, PACKAGE, PRIVATE;

  public static Visibility fromModifiers(int modifiers) {
    if (java.lang.reflect.Modifier.isPublic(modifiers)) {
      return PUBLIC;
    }
    if (java.lang.reflect.Modifier.isProtected(modifiers)) {
      return PROTECTED;
    }
    if (java.lang.reflect.Modifier.isPrivate(modifiers)) {
      return PRIVATE;
    }
    return PACKAGE;
  }
}
