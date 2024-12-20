package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.List;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext.Key;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipeline;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.args.ArgsPreprocessor;

/**
 * A {@link InvocationPipelineStep invocation pipeline step} that allows customizations to preview
 * and manipulate the arguments that will be used to create and populate the command.
 * 
 * @see InvocationPipeline
 */
public class PreprocessArgsStep extends InvocationPipelineStepBase {
  public static final Key<ArgsPreprocessor> ARGS_PREPROCESSOR_KEY = Key.of(ArgsPreprocessor.class);

  public List<String> preprocessArgs(List<String> resolvedArgs, InvocationContext context) {
    ArgsPreprocessor preprocessor = context.get(ARGS_PREPROCESSOR_KEY).orElseThrow();

    List<String> preprocessedArgs;
    try {
      getListener(context).beforePreprocessArgsStep(resolvedArgs, context);
      preprocessedArgs = doPreprocessArgs(preprocessor, resolvedArgs);
      getListener(context).afterPreprocessArgsStep(resolvedArgs, preprocessedArgs, context);
    } catch (Throwable e) {
      getListener(context).catchPreprocessArgsStep(e, context);
      throw e;
    } finally {
      getListener(context).finallyPreprocessArgsStep(context);
    }

    return preprocessor.preprocess(preprocessedArgs);
  }

  protected List<String> doPreprocessArgs(ArgsPreprocessor preprocessor,
      List<String> resolvedArgs) {
    return preprocessor.preprocess(resolvedArgs);
  }
}
