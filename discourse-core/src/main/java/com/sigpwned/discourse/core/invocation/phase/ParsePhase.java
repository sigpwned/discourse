package com.sigpwned.discourse.core.invocation.phase;

import java.util.List;
import java.util.Map;

public interface ParsePhase {

  public List<Map.Entry<String, String>> parse(Map<String, String> vocabulary,
      Map<String, String> naming, List<String> args);
}
