package com.sigpwned.discourse.core.pipeline.invocation.args.step;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.pipeline.invocation.args.ArgsInvocationContext;

public class AttributeStep {
  public List<Map.Entry<String, String>> attribute(Map<Coordinate, String> propertyNames,
      List<Map.Entry<Coordinate, String>> parsedArgs, ArgsInvocationContext context) {
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
