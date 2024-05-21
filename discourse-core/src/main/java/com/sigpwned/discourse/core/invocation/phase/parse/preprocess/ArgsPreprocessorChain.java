package com.sigpwned.discourse.core.invocation.phase.parse.preprocess;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.InvocationContext;

public class ArgsPreprocessorChain extends Chain<ArgsPreprocessor> implements ArgsPreprocessor {
  @Override
  public List<String> preprocessArgs(List<String> args, InvocationContext context) {
    for (ArgsPreprocessor preprocessor : this) {
      args = preprocessor.preprocessArgs(new ArrayList<>(args), context);
    }
    return List.copyOf(args);
  }
}
