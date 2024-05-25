package com.sigpwned.discourse.core.pipeline.invocation.args.step;

import java.util.Map;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.pipeline.invocation.args.ArgsInvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.args.ArgsInvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.args.step.preprocess.coordinates.CoordinatesPreprocessor;

public class PreprocessCoordinatesStep {
  public Map<Coordinate, String> preprocess(CoordinatesPreprocessor preprocessor,
      Map<Coordinate, String> originalCoordinates, ArgsInvocationContext context) {
    ArgsInvocationPipelineListener listener = context.getListener();

    Map<Coordinate, String> preprocessedCoordinates;
    try {
      listener.beforePreprocessCoordinatesStep(originalCoordinates);
      preprocessedCoordinates = doPreprocess(preprocessor, originalCoordinates);
      listener.afterPreprocessCoordinatesStep(originalCoordinates, preprocessedCoordinates);

    } catch (Throwable e) {
      listener.catchPreprocessCoordinatesStep(e);
      throw e;
    } finally {
      listener.finallyPreprocessCoordinatesStep();
    }

    return preprocessor.preprocess(originalCoordinates);
  }

  protected Map<Coordinate, String> doPreprocess(CoordinatesPreprocessor preprocessor,
      Map<Coordinate, String> originalCoordinates) {
    return preprocessor.preprocess(originalCoordinates);
  }
}
