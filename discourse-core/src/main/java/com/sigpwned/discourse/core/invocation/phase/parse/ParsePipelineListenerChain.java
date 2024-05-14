package com.sigpwned.discourse.core.invocation.phase.parse;

import com.sigpwned.discourse.core.Chain;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class ParsePipelineListenerChain extends Chain<ParsePipelineListener> implements
    ParsePipelineListener {

  @Override
  public void beforeParse(Map<String, String> vocabulary, List<String> commandArgs) {
    for (ParsePipelineListener listener : this) {
      listener.beforeParse(vocabulary, commandArgs);
    }
  }

  @Override
  public void afterParse(Map<String, String> vocabulary, List<String> commandArgs,
      List<Entry<Object, String>> parsedArgs) {
    for (ParsePipelineListener listener : this) {
      listener.afterParse(vocabulary, commandArgs, parsedArgs);
    }
  }

  @Override
  public void beforeGroup(Map<Object, String> propertyNames,
      List<Entry<Object, String>> parsedArgs) {
    for (ParsePipelineListener listener : this) {
      listener.beforeGroup(propertyNames, parsedArgs);
    }
  }

  @Override
  public void afterGroup(Map<Object, String> propertyNames,
      List<Map.Entry<Object, String>> parsedArgs, Map<String, List<String>> groupedArgs) {
    for (ParsePipelineListener listener : this) {
      listener.afterGroup(propertyNames, parsedArgs, groupedArgs);
    }
  }

  @Override
  public void beforeMap(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs) {
    for (ParsePipelineListener listener : this) {
      listener.beforeMap(mappers, groupedArgs);
    }
  }

  @Override
  public void afterMap(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs, Map<String, List<Object>> mappedArgs) {
    for (ParsePipelineListener listener : this) {
      listener.afterMap(mappers, groupedArgs, mappedArgs);
    }
  }

  @Override
  public void beforeReduce(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs) {
    for (ParsePipelineListener listener : this) {
      listener.beforeReduce(reducers, mappedArgs);
    }
  }

  @Override
  public void afterReduce(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs, Map<String, Object> reducedArgs) {
    for (ParsePipelineListener listener : this) {
      listener.afterReduce(reducers, mappedArgs, reducedArgs);
    }
  }
}
