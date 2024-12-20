package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import com.sigpwned.discourse.core.exception.internal.SinkFailureInternalDiscourseException;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;

/**
 * A {@link InvocationPipelineStep invocation pipeline step} that converts the mapped arguments into
 * a single value that will be be used to create and initialize the command. For example, this step
 * might simply choose the first given value, or create a list from all given values.
 * 
 * @link InvocationPipeline
 */
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
      String propertyName = entry.getKey();
      List<Object> values = entry.getValue();

      Function<List<Object>, Object> reducer = reducers.get(propertyName);

      Object reducedValue;
      try {
        reducedValue = reducer.apply(values);
      } catch (Exception e) {
        throw new SinkFailureInternalDiscourseException(propertyName, e);
      }


      result.put(propertyName, reducedValue);
    }

    return result;
  }
}
