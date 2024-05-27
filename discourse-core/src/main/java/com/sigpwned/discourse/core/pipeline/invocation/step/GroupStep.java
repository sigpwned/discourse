package com.sigpwned.discourse.core.pipeline.invocation.step;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;

public class GroupStep extends InvocationPipelineStepBase {
  public Map<String, List<String>> group(List<Map.Entry<String, String>> attributedArgs,
      InvocationContext context) {
    Map<String, List<String>> groupedArgs;

    try {
      getListener(context).beforeGroupStep(attributedArgs);
      groupedArgs = doGroup(attributedArgs);
      getListener(context).afterGroupStep(attributedArgs, groupedArgs);
    } catch (Throwable problem) {
      getListener(context).catchGroupStep(problem);
      throw problem;
    } finally {
      getListener(context).finallyGroupStep();
    }

    return unmodifiableMap(groupedArgs);
  }

  protected Map<String, List<String>> doGroup(List<Map.Entry<String, String>> attributedArgs) {
    Map<String, List<String>> result = new HashMap<>();

    for (Map.Entry<String, String> attributedArg : attributedArgs) {
      String propertyName = attributedArg.getKey();
      if (!result.containsKey(propertyName)) {
        result.put(propertyName, new ArrayList<>());
      }
    }

    for (Map.Entry<String, String> attributedArg : attributedArgs) {
      String propertyName = attributedArg.getKey();
      String propertyValue = attributedArg.getValue();
      result.get(propertyName).add(propertyValue);
    }

    for (String propertyName : result.keySet()) {
      result.put(propertyName, unmodifiableList(result.get(propertyName)));
    }

    return unmodifiableMap(result);
  }
}
