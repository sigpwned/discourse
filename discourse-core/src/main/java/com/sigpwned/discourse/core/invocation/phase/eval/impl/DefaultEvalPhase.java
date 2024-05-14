package com.sigpwned.discourse.core.invocation.phase.eval.impl;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.CommandBody;
import com.sigpwned.discourse.core.command.CommandProperty;
import com.sigpwned.discourse.core.invocation.phase.EvalPhase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultEvalPhase implements EvalPhase {

  @Override
  public <T> Map<String, Object> eval(Command<T> command,
      List<Map.Entry<String, String>> parsedArgs) {
    CommandBody<T> body = command.getBody().orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("Command has no body");
    });

    Map<String, List<String>> groupedArgs = groupStep(parsedArgs);

    Map<String, List<Object>> mappedArgs = mapStep(body.getProperties().stream()
            .collect(Collectors.toMap(CommandProperty::getName, CommandProperty::getMapper)),
        groupedArgs);

    Map<String, Object> reducedArgs = reduceStep(body.getProperties().stream()
            .collect(Collectors.toMap(CommandProperty::getName, CommandProperty::getReducer)),
        mappedArgs);

    return reducedArgs;
  }

  protected <T> Map<String, List<String>> groupStep(List<Map.Entry<String, String>> parsedArgs) {
    Map<String, List<String>> groupedArgs = new HashMap<>();

    for (Map.Entry<String, String> parsedArg : parsedArgs) {
      String key = parsedArg.getKey();
      String value = parsedArg.getValue();

      List<String> values = groupedArgs.computeIfAbsent(key, k -> new ArrayList<>());

      values.add(value);
    }

    return groupedArgs;
  }

  protected <T> Map<String, List<Object>> mapStep(Map<String, Function<String, Object>> mappers,
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

  protected <T> Map<String, Object> reduceStep(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs) {
    Map<String, Object> reducedArgs = new HashMap<>(mappedArgs.size());

    for (Map.Entry<String, List<Object>> entry : mappedArgs.entrySet()) {
      String name = entry.getKey();
      List<Object> mappedValues = entry.getValue();

      Function<List<Object>, Object> reducer = Optional.ofNullable(reducers.get(name))
          .orElseThrow(() -> {
            // TODO better exception
            return new IllegalArgumentException("no reducer for " + name);
          });

      Object reducedValue = reducer.apply(mappedValues);

      reducedArgs.put(name, reducedValue);
    }

    return reducedArgs;
  }
}
