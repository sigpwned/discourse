package com.sigpwned.discourse.core.phase.assemble;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface AssemblePhase {

  public static final String TARGET = "";

  public Object assemble(List<Consumer<Map<String, List<Object>>>> steps,
      Map<String, List<Object>> arguments);
}
