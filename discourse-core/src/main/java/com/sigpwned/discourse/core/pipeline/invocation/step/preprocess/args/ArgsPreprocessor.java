package com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.args;

import java.util.List;

public interface ArgsPreprocessor {
  public List<String> preprocess(List<String> resolvedArgs);
}
