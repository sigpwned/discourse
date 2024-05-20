package com.sigpwned.discourse.core.invocation.phase.parse.preprocess;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.Chain;

public class ArgsPreprocessorChain extends Chain<ArgsPreprocessor> implements ArgsPreprocessor {
  @Override
  public List<String> preprocessArgs(List<String> args) {
    for (ArgsPreprocessor preprocessor : this) {
      args = preprocessor.preprocessArgs(new ArrayList<>(args));
    }
    return List.copyOf(args);
  }
}
