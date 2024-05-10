package com.sigpwned.discourse.core.invocation;

import com.sigpwned.discourse.core.invocation.phase.scan.Command;
import java.util.List;
import java.util.Map;

public interface ParsePhase {

  public <T> Map<String, Object> parse(Command<T> command, List<String> args);
}
