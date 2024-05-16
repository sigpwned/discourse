package com.sigpwned.discourse.core.invocation.phase.eval.impl;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import com.sigpwned.discourse.core.invocation.phase.EvalPhase;

public class DefaultEvalPhase implements EvalPhase {
  private final DefaultEvalPhaseListener listener;

  public DefaultEvalPhase(DefaultEvalPhaseListener listener) {
    this.listener = requireNonNull(listener);
  }

  @Override
  public final Map<String, Object> eval(Map<String, Function<String, Object>> mappers,
      Map<String, Function<List<Object>, Object>> reducers,
      List<Map.Entry<String, String>> parsedArgs) {
    Map<String, List<String>> groupedArgs = doGroupStep(parsedArgs);


    Map<String, List<Object>> mappedArgs = doMapStep(mappers, groupedArgs);


    Map<String, Object> reducedArgs = doReduceStep(reducers, mappedArgs);

    return reducedArgs;
  }

  private Map<String, List<String>> doGroupStep(List<Entry<String, String>> parsedArgs) {
    Map<String, List<String>> groupedArgs;
    try {
      getListener().beforeEvalPhaseGroupStep(parsedArgs);
      groupedArgs = groupStep(parsedArgs);
      getListener().afterEvalPhaseGroupStep(parsedArgs, groupedArgs);
    } catch (Throwable problem) {
      getListener().catchEvalPhaseGroupStep(problem);
      throw problem;
    } finally {
      getListener().finallyEvalPhaseGroupStep();
    }
    return groupedArgs;
  }


  protected Map<String, List<String>> groupStep(List<Map.Entry<String, String>> parsedArgs) {
    Map<String, List<String>> groupedArgs = new HashMap<>();

    for (Map.Entry<String, String> parsedArg : parsedArgs) {
      String key = parsedArg.getKey();
      String value = parsedArg.getValue();

      List<String> values = groupedArgs.computeIfAbsent(key, k -> new ArrayList<>());

      values.add(value);
    }

    return groupedArgs;
  }

  private Map<String, List<Object>> doMapStep(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs) {
    Map<String, List<Object>> mappedArgs;
    try {
      getListener().beforeEvalPhaseEvalStep(mappers, groupedArgs);
      mappedArgs = mapStep(mappers, groupedArgs);
      getListener().afterEvalPhaseEvalStep(mappers, groupedArgs, mappedArgs);
    } catch (Throwable problem) {
      getListener().catchEvalPhaseEvalStep(problem);
      throw problem;
    } finally {
      getListener().finallyEvalPhaseEvalStep();
    }
    return mappedArgs;
  }

  protected Map<String, List<Object>> mapStep(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs) {
    Map<String, List<Object>> mappedArgs = new HashMap<>(groupedArgs.size());

    for (Map.Entry<String, List<String>> entry : groupedArgs.entrySet()) {
      String name = entry.getKey();
      List<String> stringValues = entry.getValue();

      Function<String, Object> mapper = Optional.ofNullable(mappers.get(name)).orElseThrow(() -> {
        // TODO better exception
        return new IllegalArgumentException("no mapper for " + name);
      });

      List<Object> mappedValues = new ArrayList<>();
      for (String stringValue : stringValues) {
        Object mappedValue = mapper.apply(stringValue);
        mappedValues.add(mappedValue);
      }

      mappedArgs.put(name, mappedValues);
    }

    return mappedArgs;
  }

  private Map<String, Object> doReduceStep(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs) {
    Map<String, Object> reducedArgs;
    try {
      getListener().beforeEvalPhaseReduceStep(reducers, mappedArgs);
      reducedArgs = reduceStep(reducers, mappedArgs);
      getListener().afterEvalPhaseReduceStep(reducers, mappedArgs, reducedArgs);
    } catch (Throwable problem) {
      getListener().catchEvalPhaseReduceStep(problem);
      throw problem;
    } finally {
      getListener().finallyEvalPhaseReduceStep();
    }
    return reducedArgs;
  }

  protected Map<String, Object> reduceStep(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs) {
    Map<String, Object> reducedArgs = new HashMap<>(mappedArgs.size());

    for (Map.Entry<String, List<Object>> entry : mappedArgs.entrySet()) {
      String name = entry.getKey();
      List<Object> mappedValues = entry.getValue();

      Function<List<Object>, Object> reducer =
          Optional.ofNullable(reducers.get(name)).orElseThrow(() -> {
            // TODO better exception
            return new IllegalArgumentException("no reducer for " + name);
          });

      Object reducedValue = reducer.apply(mappedValues);

      reducedArgs.put(name, reducedValue);
    }

    return reducedArgs;
  }

  private DefaultEvalPhaseListener getListener() {
    return listener;
  }
}
