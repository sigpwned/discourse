package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;

public class ReduceStep extends InvocationPipelineStepBase {
  public Map<String, Object> reduce(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs, InvocationContext context) {
    Map<String, Object> reducedArgs;

    try {
      getListener(context).beforeReduceStep(mappedArgs, context);
      reducedArgs = doReduce(reducers, mappedArgs);
      getListener(context).afterReduceStep(mappedArgs, reducedArgs, context);
    } catch (Exception e) {
      getListener(context).catchReduceStep(e, context);
      throw e;
    } finally {
      getListener(context).finallyReduceStep(context);
    }

    return reducedArgs;
  }

  protected Map<String, Object> doReduce(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs) {
    Map<String, Object> result = new HashMap<>();

    for (Map.Entry<String, List<Object>> entry : mappedArgs.entrySet()) {
      String name = entry.getKey();
      List<Object> values = entry.getValue();
      Function<List<Object>, Object> reducer = reducers.get(name);
      Object reducedValue = reducer.apply(values);
      result.put(name, reducedValue);
    }

    return result;
  }
}
