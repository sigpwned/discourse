package com.sigpwned.discourse.core.invocation.phase.eval.impl;

import com.sigpwned.discourse.core.invocation.model.command.Command;
import com.sigpwned.discourse.core.invocation.phase.EvalPhase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DefaultEvalPhase implements EvalPhase {

  @Override
  public <T> Map<String, Object> eval(Command<T> command,
      List<Map.Entry<String, String>> parsedArgs) {

    Map<String, List<String>> groupedArgs = groupStep(command, parsedArgs);

    Map<String, List<Object>> mappedArgs = mapStep(command, groupedArgs);

    Map<String, Object> reducedArgs = reduceStep(command, mappedArgs);

    return reducedArgs;
  }

  protected <T> Map<String, List<String>> groupStep(Command<T> command,
      List<Map.Entry<String, String>> parsedArgs) {
    Map<String, List<String>> groupedArgs = new HashMap<>();

    for (Map.Entry<String, String> parsedArg : parsedArgs) {
      String key = parsedArg.getKey();
      String value = parsedArg.getValue();

      List<String> values = groupedArgs.computeIfAbsent(key, k -> new ArrayList<>());

      values.add(value);
    }

    return groupedArgs;
  }

  protected <T> Map<String, List<Object>> mapStep(Command<T> command,
      Map<String, List<String>> groupedArgs) {
    Map<String, List<Object>> mappedArgs = new HashMap<>(groupedArgs.size());

    for (Map.Entry<String, List<String>> entry : groupedArgs.entrySet()) {
      String name = entry.getKey();
      List<String> stringValues = entry.getValue();

      Function<String, Object> mapper = command.getMapper(name).orElseThrow(() -> {
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

  protected <T> Map<String, Object> reduceStep(Command<T> command,
      Map<String, List<Object>> mappedArgs) {
    Map<String, Object> reducedArgs = new HashMap<>(mappedArgs.size());

    for (Map.Entry<String, List<Object>> entry : mappedArgs.entrySet()) {
      String name = entry.getKey();
      List<Object> mappedValues = entry.getValue();

      Function<List<Object>, Object> reducer = command.getReducer(name).orElseThrow(() -> {
        // TODO better exception
        return new IllegalArgumentException("no reducer for " + name);
      });

      Object reducedValue = reducer.apply(mappedValues);

      reducedArgs.put(name, reducedValue);
    }

    return reducedArgs;
  }
}
