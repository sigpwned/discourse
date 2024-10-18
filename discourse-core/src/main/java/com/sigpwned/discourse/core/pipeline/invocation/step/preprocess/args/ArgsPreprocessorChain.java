package com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.args;

import java.util.List;
import com.sigpwned.discourse.core.Chain;

public class ArgsPreprocessorChain extends Chain<ArgsPreprocessor> implements ArgsPreprocessor {
  @Override
  public List<String> preprocess(List<String> resolvedArgs) {
    for (ArgsPreprocessor preprocessor : this) {
      resolvedArgs = preprocessor.preprocess(resolvedArgs);
    }
    return resolvedArgs;
  }
}
