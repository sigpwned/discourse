package com.sigpwned.discourse.core.phase.resolve.impl;

import com.sigpwned.discourse.core.phase.resolve.ResolvePhase;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Selects a choice from the given choices based on the longest matching prefix of the arguments.
 */
public class DefaultResolvePhase implements ResolvePhase {

  /**
   * <p>
   * Selects a choice from the given choices based on the longest matching prefix of the arguments.
   * </p>
   *
   * <pre>
   *   [a] &rarr; A
   *   [a, b] &rarr; AB
   *   [a, b, c] &rarr; ABC
   *   [x] &rarr; X
   *   [x, y] &rarr; XY
   * </pre>
   *
   * <p>
   * and the arguments:
   * </p>
   *
   * <pre>
   *   [a, b, d, e]
   * </pre>
   *
   * <p>
   * the result would be:
   * </p>
   *
   * <pre>
   *   AB
   * </pre>
   *
   * @param choices   the choices
   * @param arguments the arguments
   * @return The value associated with the longest matching prefix.
   */
  public Optional<Object> resolve(Map<List<String>, Object> choices, List<String> arguments) {
    Object result = null;
    for (int i = 0; i < arguments.size(); i++) {
      List<String> prefix = arguments.subList(0, i);
      if (choices.containsKey(prefix)) {
        result = choices.get(prefix);
      }
    }
    return Optional.ofNullable(result);
  }
}
