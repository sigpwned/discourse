package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import com.sigpwned.discourse.core.exception.user.InvalidArgumentUserDiscourseException;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;

/**
 * A {@link InvocationPipelineStep invocation pipeline step} that maps the grouped command line
 * arguments from their string representations to their actual values, i.e., deserialization.
 * 
 * @link InvocationPipeline
 */
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
      String propertyName = entry.getKey();
      List<String> stringValues = entry.getValue();

      Function<String, Object> mapper = mappers.get(propertyName);

      List<Object> mappedValues = new ArrayList<>(stringValues.size());
      for (String stringValue : stringValues) {
        Object mappedValue;
        try {
          mappedValue = mapper.apply(stringValue);
        } catch (Exception e) {
          // TODO We need a coordinate here, please
          throw new InvalidArgumentUserDiscourseException(propertyName, null, stringValue, e);
        }
        mappedValues.add(mappedValue);
      }

      result.put(propertyName, mappedValues);
    }

    return result;
  }
}
