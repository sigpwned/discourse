package com.sigpwned.discourse.core.pipeline.invocation.args.step;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.pipeline.invocation.args.ArgsInvocationContext;

public class GroupStep {
  public Map<String, List<String>> step(List<Map.Entry<String, String>> attributedArgs,
      ArgsInvocationContext context) {
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
