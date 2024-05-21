package com.sigpwned.discourse.core.invocation.phase.parse.preprocess;

import java.util.HashMap;
import java.util.Map;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.args.Coordinate;

public class CoordinatesPreprocessorChain extends Chain<CoordinatesPreprocessor>
    implements CoordinatesPreprocessor {
  public Map<Coordinate, String> preprocessCoordinates(Map<Coordinate, String> coordinates,
      InvocationContext context) {
    for (CoordinatesPreprocessor preprocessor : this) {
      coordinates = preprocessor.preprocessCoordinates(new HashMap<>(coordinates), context);
    }
    return Map.copyOf(coordinates);
  }
}
