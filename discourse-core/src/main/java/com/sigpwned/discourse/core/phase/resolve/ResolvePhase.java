package com.sigpwned.discourse.core.phase.resolve;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ResolvePhase {

  /**
   * Selects one choice from the given choices based on the given arguments. Implementations are
   * free to use whatever policy they want to perform the selection. The result is empty if no
   * suitable choice is found.
   *
   * @param choices   the choices
   * @param arguments the arguments
   * @return The selection. The result is empty if no suitable choice is found.
   */
  public Optional<Object> resolve(Map<List<String>, Object> choices, List<String> arguments);
}
