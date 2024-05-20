package com.sigpwned.discourse.core.invocation.phase.parse.preprocess;

import java.util.List;

public interface ArgsPreprocessor {
  public List<String> preprocessArgs(List<String> args);
}
