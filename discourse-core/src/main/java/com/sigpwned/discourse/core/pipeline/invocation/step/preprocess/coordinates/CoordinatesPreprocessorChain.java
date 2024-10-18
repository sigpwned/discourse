package com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.coordinates;

import java.util.HashMap;
import java.util.Map;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.args.Coordinate;

public class CoordinatesPreprocessorChain extends Chain<CoordinatesPreprocessor>
    implements CoordinatesPreprocessor {

  @Override
  public Map<Coordinate, String> preprocess(Map<Coordinate, String> originalCoordinates) {
    Map<Coordinate, String> coordinates = new HashMap<>(originalCoordinates);
    for (CoordinatesPreprocessor preprocessor : this) {
      coordinates = preprocessor.preprocess(coordinates);
    }
    return coordinates;
  }
}
