package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.Map;
import java.util.function.Function;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;

public class FinishStep extends InvocationPipelineStepBase {
  public <T> T finish(Function<Map<String, Object>, T> finisher, Map<String, Object> reducedArgs,
      InvocationContext context) {
    // TODO should finisher come from context?

    T instance;

    try {
      getListener(context).beforeFinishStep(reducedArgs, context);
      instance = doFinish(finisher, reducedArgs);
      getListener(context).afterFinishStep(reducedArgs, instance, context);
    } catch (Exception e) {
      getListener(context).catchFinishStep(e, context);
      throw e;
    } finally {
      getListener(context).finallyFinishStep(context);
    }

    return finisher.apply(reducedArgs);
  }

  protected <T> T doFinish(Function<Map<String, Object>, T> finisher,
      Map<String, Object> reducedArgs) {
    return finisher.apply(reducedArgs);
  }
}
