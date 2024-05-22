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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Graphs {
  private Graphs() {}

  /**
   * <p>
   * Compute the transitive closure of the given graph. That is, given the graph described by the
   * directed edges in the {@link Map map} <code>shallowDependencies</code>, compute the graph
   * <code>closure</code> such that for any node <code>n</code> in the graph,
   * <code>closure.get(n)</code> is the set of all nodes reachable from <code>n</code> in the graph
   * <code>shallowDependencies</code>.
   * </p>
   * 
   * <p>
   * More simply, given a set of direct or "shallow" dependencies, return the set of transitive or
   * "deep" dependencies.
   * </p>
   * 
   * @param <T> the type of the nodes in the graph
   * @param shallowDependencies The graph for which to compute the transitive closure represented as
   *        a map from nodes to the set of nodes they depend on.
   * @return The transitive closure of the graph.
   * 
   * @see <a href="https://en.wikipedia.org/wiki/Transitive_closure">Transitive Closure</a>
   */
  public static <T> Map<T, Set<T>> transitiveClosure(Map<T, Set<T>> shallowDependencies) {
    Map<T, Set<T>> closure = new HashMap<>();
    for (Map.Entry<T, Set<T>> e : shallowDependencies.entrySet()) {
      closure.put(e.getKey(), new HashSet<>(e.getValue()));
    }

    boolean changed;
    do {
      changed = false;
      for (Map.Entry<T, Set<T>> e : closure.entrySet()) {
        T node = e.getKey();
        Set<T> deps = e.getValue();
        for (T dep : deps) {
          if (deps.addAll(closure.get(dep)))
            changed = true;
        }
      }
    } while (changed);

    return closure;
  }


  /**
   * <p>
   * A {@link Comparator} that implements the toplogical ordering of the given graph. That is, the
   * ordering places an element <em>after</em> any other elements it depends on. Specifically, it
   * induces an ordering on the elements such that:
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
   * The given graph must be acyclic. If a circular dependency is detected, then an
   * <code>IllegalArgumentException</code> is thrown. Remember that self-dependencies are cyclic.
   * </p>
   * 
   * <p>
   * Only elements of the given collection are compared. If an element is not in the collection,
   * then an <code>IllegalArgumentException</code> is thrown.
   * </p>
   * 
   * <p>
   * An element may have no dependencies, which would be represented as an entry in the map with an
   * empty collection as the value. Such elements are necessarily considered less than or equal to
   * all other elements.
   * </p>
   * 
   * <p>
   * <code>null</code> elements and dependencies are not allowed. If an element is <code>null</code>
   * or if an element depends on <code>null</code>, then a <code>NullPointerException</code> is
   * thrown.
   * </p>
   * 
   * <p>
   * The comparator is consistent with equals, but not necessarily with the natural ordering of the
   * elements. The comparator is not serializable.
   * </p>
   * 
   * @param <T> The type of elements to compare.
   * @param graph A map of elements to their direct dependencies. The map must not contain any
   *        circular dependencies. The union of the value space should be a subset of the key space.
   *        None of <code>null</code> keys, <code>null</code> collections, or <code>null</code>
   *        elements in those collections are allowed. The map is not modified by this method.
   * @return A comparator that compares elements based on their mutual dependencies.
   * @throws NullPointerException if an element is <code>null</code> or if an element depends on
   *         <code>null</code>
   * @throws IllegalArgumentException if a circular dependency is detected, including a
   *         self-dependency
   * @throws IllegalArgumentException if an element is not in the collection
   * @see <a href="https://en.wikipedia.org/wiki/Topological_sorting">Topological Sorting</a>
   * @see <a href="https://en.wikipedia.org/wiki/Directed_acyclic_graph">Directed Acyclic Graph</a>
   * @see <a href="https://en.wikipedia.org/wiki/Transitive_closure">Transitive Closure</a>
   * @see #transitiveClosure(Map)
   */
  public static <T> Comparator<T> topologicalOrdering(Map<T, Set<T>> graph) {
    final Map<T, Set<T>> closure = transitiveClosure(graph);
    return new Comparator<T>() {
      public int compare(T a, T b) {
        if (a == null)
          throw new NullPointerException();
        if (b == null)
          throw new NullPointerException();

        final Collection<T> aDependencies = closure.get(a);
        if (aDependencies == null) {
          if (closure.containsKey(a))
            throw new NullPointerException();
          throw new IllegalArgumentException(a + " is not in the collection");
        }

        final Collection<T> bDependencies = closure.get(b);
        if (bDependencies == null) {
          if (closure.containsKey(b))
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
