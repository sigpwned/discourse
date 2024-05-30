package com.sigpwned.discourse.core.pipeline.invocation.step.postprocess.args;

import java.util.Map;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class ArgsPostprocessorChain extends Chain<ArgsPostprocessor>
    implements ArgsPostprocessor {
  @Override
  public Map<String, Object> postprocessProperties(Map<String, Object> properties,
      InvocationContext context) {
    for (ArgsPostprocessor postprocessor : this) {
      properties = postprocessor.postprocessProperties(properties, context);
    }
    return properties;
  }
}
