package com.sigpwned.discourse.core.phase2;

import com.sigpwned.discourse.core.invocation.phase.parse.ParsePipelineListener;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.ParsePhase;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class FooPhaseListener implements ParsePipelineListener {

  private final FooFactory factory;

  @Override
  public void beforeParse(Map<String, String> vocabulary, List<String> commandArgs) {
    // Register all our syntax. The syntax to add is determined by the
    for (FooAttribute attribute : factory.getAttributes()) {
      for (Map.Entry<Object, String> coordinate : attribute.getCoordinates().entrySet()) {
        if (coordinate.getValue().equals(ParsePhase.FLAG_TYPE)) {
          vocabulary.put(coordinate.getKey().toString(), ParsePhase.FLAG_TYPE);
        } else if (coordinate.getValue().equals(ParsePhase.OPTION_TYPE)) {
          vocabulary.put(coordinate.getKey().toString(), ParsePhase.OPTION_TYPE);
        }
      }
    }
  }

  @Override
  public void beforeCorrelate(Map<Object, String> propertyNames,
      List<Map.Entry<Object, String>> parsedArgs) {
    // Correlate the parsed arguments with the factory's attributes
    for (FooAttribute attribute : factory.getAttributes()) {
      for (Map.Entry<Object, String> coordinate : attribute.getCoordinates().entrySet()) {
        propertyNames.put(coordinate.getKey(), attribute.getName());
      }
    }
  }

  @Override
  public void beforeGroup(List<Map.Entry<String, String>> correlatedArgs) {
    // I don't think we have anything to do here...?
  }

  @Override
  public void beforeMap(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs) {
    for (FooAttribute attribute : factory.getAttributes()) {
      mappers.put(attribute.getName(), attribute.getMapper());
    }
  }

  @Override
  public void beforeReduce(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs) {
    for (FooAttribute attribute : factory.getAttributes()) {
      reducers.put(attribute.getName(), attribute.getReducer());
    }
  }

  @Override
  public void beforeAssemble(List<Consumer<Map<String, Object>>> steps,
      Map<String, Object> reducedArgs) {
    for (Consumer<Map<String, Object>> assemblyStep : factory.getAssemblySteps()) {
      steps.add(assemblyStep);
    }
  }
}
