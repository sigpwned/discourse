package com.sigpwned.discourse.core.phase2;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toUnmodifiableMap;

import com.sigpwned.discourse.core.phase.assemble.AssemblePhase;
import com.sigpwned.discourse.core.util.MoreSets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class FooFactory {

  public static final String TARGET = AssemblePhase.TARGET;

  /**
   * <p>
   * Create an impolite assembly step from a factory function.
   * </p>
   *
   * <p>
   * This method calls the factory function with the given map. The factory function is expected to
   * return the result of the assembly step. Finally, this method adds the result of the assembly
   * step to the map with the key {@link AssemblePhase#TARGET}.
   * </p>
   *
   * <p>
   * The end result is that the result of the assembly step is "produced" and added to the map with
   * the key {@link AssemblePhase#TARGET}.
   * </p>
   *
   * <p>
   * This assembly step is considered "impolite" because it can see all of the arguments in the
   * given map, and it does not remove any of the arguments from the map after it is done with
   * them.
   * </p>
   *
   * @param factoryFunction the factory function
   * @return an impolite assembly step
   */
  public static Consumer<Map<String, Object>> impoliteAssemblyStep(
      Function<Map<String, Object>, Object> factoryFunction) {
    return arguments -> {
      Object result = factoryFunction.apply(unmodifiableMap(arguments));
      arguments.put(TARGET, result);
    };
  }

  /**
   * <p>
   * Create a polite assembly step from a list of expected arguments and a factory function.
   * </p>
   *
   * <p>
   * First, this method creates a copy of the given map containing only the expected arguments.
   * Next, it calls the factory function with the copy of the map. The factory function is expected
   * to return the result of the assembly step. Finally, this method removes the expected arguments
   * from the given map and adds the result of the assembly step to the map with the key
   * {@link AssemblePhase#TARGET}.
   * </p>
   *
   * <p>
   * The end result is that the expected arguments are "consumed" and removed from the given map,
   * and the result of the assembly step is "produced" and added to the map with the key
   * {@link AssemblePhase#TARGET}.
   * </p>
   *
   * <p>
   * This assembly step is considered "polite" because it can only see the expected arguments, and
   * it removes the expected arguments from the given map after it is done with them.
   * </p>
   *
   * @param expectedArgNames the names of the expected arguments
   * @param factoryFunction  the factory function
   * @return a polite assembly step
   * @throws IllegalArgumentException if the given arguments do not contain all of the expected
   *                                  arguments.
   */
  public static Consumer<Map<String, Object>> politeAssemblyStep(Set<String> expectedArgNames,
      Function<Map<String, Object>, Object> factoryFunction) {
    final Consumer<Map<String, Object>> impoliteAssemblyStep = impoliteAssemblyStep(
        factoryFunction);
    return arguments -> {
      if (!arguments.keySet().containsAll(expectedArgNames)) {
        // TODO better exception
        throw new IllegalArgumentException(
            "missing arguments: " + MoreSets.difference(expectedArgNames, arguments.keySet()));
      }

      impoliteAssemblyStep.accept(
          expectedArgNames.stream().map(argName -> Map.entry(argName, arguments.get(argName)))
              .collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue)));

      arguments.keySet().removeAll(expectedArgNames);
    };
  }

  private final List<FooAttribute> attributes;
  private final List<Consumer<Map<String, Object>>> assemblySteps;

  public FooFactory(List<FooAttribute> attributes,
      Function<Map<String, Object>, Object> factoryFunction) {
    this(attributes, List.of(impoliteAssemblyStep(factoryFunction)));
  }

  public FooFactory(List<FooAttribute> attributes,
      List<Consumer<Map<String, Object>>> assemblySteps) {
    this.attributes = unmodifiableList(attributes);
    this.assemblySteps = unmodifiableList(assemblySteps);
  }

  public List<FooAttribute> getAttributes() {
    return attributes;
  }

  public List<Consumer<Map<String, Object>>> getAssemblySteps() {
    return assemblySteps;
  }
}
