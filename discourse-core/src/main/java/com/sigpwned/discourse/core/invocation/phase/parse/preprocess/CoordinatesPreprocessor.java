package com.sigpwned.discourse.core.invocation.phase.parse.preprocess;

import java.util.Map;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.args.Coordinate;

public interface CoordinatesPreprocessor {
  public Map<Coordinate, String> preprocessCoordinates(Map<Coordinate, String> coordinates,
      InvocationContext context);
}
