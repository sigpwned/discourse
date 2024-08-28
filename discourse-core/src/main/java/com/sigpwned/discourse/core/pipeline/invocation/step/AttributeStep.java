package com.sigpwned.discourse.core.pipeline.invocation.step;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.exception.InternalDiscourseException;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;

/**
 * A {@link InvocationPipelineStep invocation pipeline step} that attributes the parsed arguments to
 * the appropriate property names.
 * 
 * @link InvocationPipeline
 */
public class AttributeStep extends InvocationPipelineStepBase {
  public List<Map.Entry<String, String>> attribute(Map<Coordinate, String> propertyNames,
      List<Map.Entry<Coordinate, String>> parsedArgs, InvocationContext context) {
    List<Map.Entry<String, String>> attributedArgs;

    try {
      getListener(context).beforeAttributeStep(parsedArgs, context);
      attributedArgs = doAttribute(propertyNames, parsedArgs);
      getListener(context).afterAttributeStep(parsedArgs, attributedArgs, context);
    } catch (Throwable problem) {
      getListener(context).catchAttributeStep(problem, context);
      throw problem;
    } finally {
      getListener(context).finallyAttributeStep(context);
    }

    return unmodifiableList(attributedArgs);
  }

  protected List<Map.Entry<String, String>> doAttribute(Map<Coordinate, String> propertyNames,
      List<Map.Entry<Coordinate, String>> parsedArgs) {
    List<Map.Entry<String, String>> result = new ArrayList<>();

    for (Map.Entry<Coordinate, String> parsedArg : parsedArgs) {
      Coordinate coordinate = parsedArg.getKey();
      String propertyName = propertyNames.get(coordinate);
      if (propertyName == null) {
        // This means we have an argument that we don't know what to do with. This is technically a
        // framework exception, in that the problem is happening in the framework and it's (likely)
        // not the application developer's direct fault, but the error probably likes in a third-
        // party module that the user has included. We'll treat this as a framework exception here,
        // but maybe we should hint at a module problem in the message.
        throw new InternalDiscourseException("no property name for " + coordinate);
      }
      result.add(Map.entry(propertyName, parsedArg.getValue()));
    }

    return unmodifiableList(result);
  }
}
