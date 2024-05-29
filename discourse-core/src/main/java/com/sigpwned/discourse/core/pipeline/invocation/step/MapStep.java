package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;

public class MapStep extends InvocationPipelineStepBase {
  public Map<String, List<Object>> map(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs, InvocationContext context) {
    Map<String, List<Object>> mappedArgs = new HashMap<>();

    try {
      getListener(context).beforeMapStep(groupedArgs, context);
      mappedArgs = doMap(mappers, groupedArgs);
      getListener(context).afterMapStep(groupedArgs, mappedArgs, context);
    } catch (Exception e) {
      getListener(context).catchMapStep(e, context);
      throw e;
    } finally {
      getListener(context).finallyMapStep(context);
    }

    return mappedArgs;
  }

  protected Map<String, List<Object>> doMap(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs) {
    Map<String, List<Object>> result = new HashMap<>();

    for (Map.Entry<String, List<String>> entry : groupedArgs.entrySet()) {
      String name = entry.getKey();
      List<String> values = entry.getValue();
      Function<String, Object> mapper = mappers.get(name);
      List<Object> mappedValues = values.stream().map(mapper).collect(Collectors.toList());
      result.put(name, mappedValues);
    }

    return result;
  }
}
