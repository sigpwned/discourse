package com.sigpwned.discourse.core.pipeline.invocation.step.postprocess.args;

import java.util.Map;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface ArgsPostprocessor {
  public Map<String, Object> postprocessProperties(Map<String, Object> properties,
      InvocationContext context);
}
