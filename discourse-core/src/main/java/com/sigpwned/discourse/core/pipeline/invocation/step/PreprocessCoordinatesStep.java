package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.Map;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.coordinates.CoordinatesPreprocessor;

public class PreprocessCoordinatesStep extends InvocationPipelineStepBase {
  public static final InvocationContext.Key<CoordinatesPreprocessor> COORDINATES_PREPROCESSOR_KEY =
      InvocationContext.Key.of(CoordinatesPreprocessor.class);

  public Map<Coordinate, String> preprocess(Map<Coordinate, String> originalCoordinates,
      InvocationContext context) {
    CoordinatesPreprocessor preprocessor =
        context.get(COORDINATES_PREPROCESSOR_KEY).orElseThrow(() -> {
          // TODO better exception
          return new IllegalStateException("No coordinates preprocessor");
        });

    Map<Coordinate, String> preprocessedCoordinates;
    try {
      getListener(context).beforePreprocessCoordinatesStep(originalCoordinates);
      preprocessedCoordinates = doPreprocess(preprocessor, originalCoordinates);
      getListener(context).afterPreprocessCoordinatesStep(originalCoordinates,
          preprocessedCoordinates);
    } catch (Throwable e) {
      getListener(context).catchPreprocessCoordinatesStep(e);
      throw e;
    } finally {
      getListener(context).finallyPreprocessCoordinatesStep();
    }

    return preprocessor.preprocess(originalCoordinates);
  }

  protected Map<Coordinate, String> doPreprocess(CoordinatesPreprocessor preprocessor,
      Map<Coordinate, String> originalCoordinates) {
    return preprocessor.preprocess(originalCoordinates);
  }
}
