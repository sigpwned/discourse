package com.sigpwned.discourse.core.pipeline.invocation.args.step.preprocess.coordinates;

import java.util.Map;
import com.sigpwned.discourse.core.args.Coordinate;

public interface CoordinatesPreprocessor {
  public Map<Coordinate, String> preprocess(Map<Coordinate, String> originalCoordinates);
}
