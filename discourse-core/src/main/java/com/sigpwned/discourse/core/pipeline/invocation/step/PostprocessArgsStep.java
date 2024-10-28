package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.Map;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipeline;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.postprocess.args.ArgsPostprocessor;

/**
 * A {@link InvocationPipelineStep invocation pipeline step} that postprocesses the reduced
 * arguments to produce the final arguments to be used in the command invocation.
 * 
 * @see InvocationPipeline
 */
public class PostprocessArgsStep extends InvocationPipelineStepBase {
  public static final InvocationContext.Key<ArgsPostprocessor> ARGS_POSTPROCESSOR_KEY =
      InvocationContext.Key.of(ArgsPostprocessor.class);

  public Map<String, Object> postprocessArgs(Map<String, Object> reducedArgs,
      InvocationContext context) {
    ArgsPostprocessor postprocessor = context.get(ARGS_POSTPROCESSOR_KEY).orElseThrow();

    Map<String, Object> postprocessedArgs;
    try {
      getListener(context).beforePostprocessPropertiesStep(reducedArgs, context);
      postprocessedArgs = doPostprocessArgs(postprocessor, reducedArgs, context);
      getListener(context).afterPostprocessPropertiesStep(reducedArgs, postprocessedArgs, context);
    } catch (Exception e) {
      getListener(context).catchPostprocessPropertiesStep(e, context);
      throw e;
    } finally {
      getListener(context).finallyPostprocessPropertiesStep(context);
    }

    return postprocessedArgs;
  }

  protected Map<String, Object> doPostprocessArgs(ArgsPostprocessor postprocessor,
      Map<String, Object> properties, InvocationContext context) {
    return postprocessor.postprocessProperties(properties, context);
  }
}
