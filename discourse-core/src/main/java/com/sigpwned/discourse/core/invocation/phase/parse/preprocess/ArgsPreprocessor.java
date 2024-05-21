package com.sigpwned.discourse.core.invocation.phase.parse.preprocess;

import java.util.List;
import com.sigpwned.discourse.core.InvocationContext;

public interface ArgsPreprocessor {
  public List<String> preprocessArgs(List<String> args, InvocationContext context);
}
