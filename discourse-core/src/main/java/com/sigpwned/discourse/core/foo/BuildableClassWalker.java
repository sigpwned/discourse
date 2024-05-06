package com.sigpwned.discourse.core.foo;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Walks a class and its superclasses, calling a visitor for each class, constructor, field, and
 * method.
 */
public class BuildableClassWalker {

  /**
   * A selector that selects a constructor from a list of constructors. May use whatever policy it
   * wants to select the constructor: annotations, default constructor, etc.
   */
  public static interface ConstructorSelector {

    /**
     * Selects a constructor from the given list of constructors.
     *
     * @param constructors the list of constructors to select from
     * @param <T>          the type of the class the constructors are for
     * @return the selected constructor, or empty if no constructor is selected
     */
    public <T> Optional<Constructor<T>> selectConstructor(List<Constructor<T>> constructors);
  }

  /**
   * A visitor that is called for each class, constructor, field, and method.
   *
   * @param <T> the type of the class being visited
   */
  public static interface Visitor<T> {

    default void beginClass(Class<? super T> clazz) {
    }

    /**
     * If the class is concrete, then this method will be called with the {@link Constructor}
     * selected by the {@link ConstructorSelector}. If the class is abstract, then this method will
     * be called with {@code null}.
     *
     * @param constructor the selected constructor, or null if the class is abstract
     */
    default void constructor(Constructor<? super T> constructor) {
    }

    default void field(Field field) {
    }

    default void method(Method method) {
    }

    default void endClass(Class<? super T> clazz) {
    }
  }

  private final ConstructorSelector constructorSelector;

  public BuildableClassWalker(ConstructorSelector constructorSelector) {
    this.constructorSelector = requireNonNull(constructorSelector);
  }

  /**
   * Walks the class hierarchy from the given class to all superclasses, calling the given visitor
   * for each class.
   *
   * @param clazz   the class to start walking from
   * @param visitor the visitor to call for each class
   * @param <T>     the type of the {@code clazz} class
   * @throws IllegalArgumentException if {@code clazz} is not abstract and has no public constructor
   *                                  selected by the given {@link ConstructorSelector}
   */
  public <T> void walkClassAndAllSuperclasses(Class<T> clazz, Visitor<T> visitor) {
    walkClassRange(clazz, null, visitor);
  }


  /**
   * Walks the class hierarchy from the given class {@code from} to the given superclass {@code to},
   * calling the given visitor for each class.
   *
   * @param from    the class to start walking from
   * @param to      the superclass to stop walking at. may be null.
   * @param visitor the visitor to call for each class
   * @param <T>     the type of the {@code from} class
   * @throws IllegalArgumentException if {@code to} is not null and is not a superclass of
   *                                  {@code from}
   * @throws IllegalArgumentException if {@code to} is not null and {@code from} and {@code to} are
   *                                  not either both interfaces or both classes
   * @throws IllegalArgumentException if {@code from} is not abstract and has no public constructor
   *                                  selected by the given {@link ConstructorSelector}
   */
  public <T> void walkClassRange(Class<T> from, Class<? super T> to, Visitor<T> visitor) {
    from = requireNonNull(from);

    if (to != null) {
      if (from.isInterface() != to.isInterface()) {
        throw new IllegalArgumentException(
            "Start and end classes must both be interfaces or both be classes");
      }
      if (!to.isAssignableFrom(from)) {
        throw new IllegalArgumentException("End class must be a superclass of start class");
      }
    }

    for (Class<? super T> current = from; current != to; current = current.getSuperclass()) {
      if (current == null) {
        // We checked that to is a superclass of from, so this should never happen.
        throw new AssertionError("End class must be a superclass of start class");
      }
      doWalkClass(current, visitor);
    }
  }

  /**
   * Walks the given class, calling the given visitor.
   *
   * @param clazz   the class to walk
   * @param visitor the visitor to call for each class
   * @param <T>     the type of the {@code clazz} class
   * @throws IllegalArgumentException if {@code clazz} is not abstract and has no public constructor
   *                                  selected by the given {@link ConstructorSelector}
   */
  public <T> void walkClass(Class<T> clazz, Visitor<T> visitor) {
    doWalkClass(clazz, visitor);
  }

  private <T> void doWalkClass(Class<? super T> clazz, Visitor<T> visitor) {
    visitor.beginClass(clazz);

    if (Modifier.isAbstract(clazz.getModifiers())) {
      visitor.constructor(null);
    } else {
      Constructor<T> selectedConstructor = constructorSelector.selectConstructor(
              Arrays.stream(clazz.getConstructors()).map(c -> (Constructor<T>) c).toList())
          .orElseThrow(() -> {
            return new IllegalArgumentException("No constructor found for " + clazz);
          });
      visitor.constructor(selectedConstructor);
    }

    for (Field field : clazz.getDeclaredFields()) {
      visitor.field(field);
    }

    for (Method method : clazz.getDeclaredMethods()) {
      visitor.method(method);
    }

    visitor.endClass(clazz);
  }

  private ConstructorSelector getConstructorSelector() {
    return constructorSelector;
  }
}
