package com.sigpwned.discourse.core.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public final class MoreComparators {
  private MoreComparators() {}


  /**
   * <p>
   * Implements a {@link Comparator} that compares elements based on their mutual acyclic
   * dependencies. In general, the ordering places an element <em>after</em> the other elements it
   * depends on. Specifically, it induces an ordering on the elements such that:
   * </p>
   * 
   * <ul>
   * <li>If <code>a</code> depends on <code>b</code> and <code>b</code> does not depend on
   * <code>a</code>, then <code>a</code> is greater than <code>b</code>.</li>
   * <li>If <code>b</code> depends on <code>a</code> and <code>a</code> does not depend on
   * <code>b</code>, then <code>b</code> is greater than <code>a</code>.</li>
   * <li>If <code>a</code> and <code>b</code> are mutually dependent, then there is a circular
   * dependency, and an <code>IllegalArgumentException</code> is thrown.</li>
   * <li>Otherwise, <code>a</code> and <code>b</code> are considered equal.</li>
   * </ul>
   * 
   * <p>
   * Only elements of the given collection are compared. If an element is not in the collection,
   * then an <code>IllegalArgumentException</code> is thrown.
   * </p>
   * 
   * <p>
   * Self-dependencies are cyclical, and therefore not allowed. If an element depends on itself,
   * then an <code>IllegalArgumentException</code> is thrown.
   * </p>
   * 
   * <p>
   * An element may have no dependencies, which would be represented as an entry in the map with an
   * empty collection as the value. Such elements are necessarily less than or equal to all other
   * elements.
   * </p>
   * 
   * <p>
   * <code>null</code> dependencies are not allowed. If an element is <code>null</code> or if an
   * element depends on <code>null</code>, then a <code>NullPointerException</code> is thrown.
   * </p>
   * 
   * <p>
   * The comparator is consistent with equals, but not necessarily with the natural ordering of the
   * elements. The comparator is not serializable.
   * </p>
   * 
   * @param <T> The type of elements to compare.
   * @param deepDependencies A map of elements to their dependencies. The dependencies are the
   *        elements that the key element depends on, both directly and transitively. The map must
   *        contain all elements that are to be compared. The map must not contain any circular
   *        dependencies. The union of the value space should be a subset of the key space. None of
   *        <code>null</code> keys, <code>null</code> collections, or <code>null</code> elements in
   *        those collections are allowed. The map is not modified by this method.
   * @return A comparator that compares elements based on their mutual dependencies.
   * @throws NullPointerException if an element is <code>null</code> or if an element depends on
   *         <code>null</code>
   * @throws IllegalArgumentException if a circular dependency is detected, including a
   *         self-dependency
   * @throws IllegalArgumentException if an element is not in the collection
   */
  public static <T> Comparator<T> directedAcyclicDependencies(
      final Map<T, ? extends Collection<T>> deepDependencies) {
    return new Comparator<T>() {
      public int compare(T a, T b) {
        if (a == null)
          throw new NullPointerException();
        if (b == null)
          throw new NullPointerException();

        final Collection<T> aDependencies = deepDependencies.get(a);
        if (aDependencies == null) {
          if (deepDependencies.containsKey(a))
            throw new NullPointerException();
          throw new IllegalArgumentException(a + " is not in the collection");
        }

        final Collection<T> bDependencies = deepDependencies.get(b);
        if (bDependencies == null) {
          if (deepDependencies.containsKey(b))
            throw new NullPointerException();
          throw new IllegalArgumentException(b + " is not in the collection");
        }

        final boolean aDependsOnB = aDependencies.contains(b);
        final boolean bDependsOnA = bDependencies.contains(a);

        if (aDependsOnB && bDependsOnA)
          throw new IllegalArgumentException("circular dependency between " + a + " and " + b);

        if (aDependsOnB) {
          return +1;
        } else if (bDependsOnA) {
          return -1;
        } else {
          return 0;
        }
      }
    };
  }
}
