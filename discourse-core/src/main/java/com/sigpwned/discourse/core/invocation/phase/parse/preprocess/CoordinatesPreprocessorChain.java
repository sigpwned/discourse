package com.sigpwned.discourse.core.invocation.phase.parse.preprocess;

import java.util.HashMap;
import java.util.Map;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.args.Coordinate;

public class CoordinatesPreprocessorChain extends Chain<CoordinatesPreprocessor>
    implements CoordinatesPreprocessor {
  public Map<Coordinate, String> preprocessCoordinates(Map<Coordinate, String> coordinates) {
    for (CoordinatesPreprocessor preprocessor : this) {
      coordinates = preprocessor.preprocessCoordinates(new HashMap<>(coordinates));
    }
    return Map.copyOf(coordinates);
  }
}
