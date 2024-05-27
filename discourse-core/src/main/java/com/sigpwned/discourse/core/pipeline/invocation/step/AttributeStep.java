package com.sigpwned.discourse.core.pipeline.invocation.step;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;

public class AttributeStep extends InvocationPipelineStepBase {
  public List<Map.Entry<String, String>> attribute(Map<Coordinate, String> propertyNames,
      List<Map.Entry<Coordinate, String>> parsedArgs, InvocationContext context) {
    List<Map.Entry<String, String>> attributedArgs;
    
    try {
      getListener(context).beforeAttributeStep(parsedArgs);
      attributedArgs = doAttribute(propertyNames, parsedArgs);
      getListener(context).afterAttributeStep(parsedArgs, attributedArgs);
    } catch (Throwable problem) {
      getListener(context).catchAttributeStep(problem);
      throw problem;
    } finally {
      getListener(context).finallyAttributeStep();
    }
    
    return unmodifiableList(attributedArgs);
  }

  protected List<Map.Entry<String, String>> doAttribute(Map<Coordinate, String> propertyNames,
      List<Map.Entry<Coordinate, String>> parsedArgs) {
    List<Map.Entry<String, String>> result = new ArrayList<>();

    for (Map.Entry<Coordinate, String> parsedArg : parsedArgs) {
      Coordinate coordinate = parsedArg.getKey();
      String propertyName = propertyNames.get(coordinate);
      if (propertyName == null) {
        throw new IllegalArgumentException("no property name for " + coordinate);
      }
      result.add(Map.entry(propertyName, parsedArg.getValue()));
    }

    return unmodifiableList(result);
  }
}
