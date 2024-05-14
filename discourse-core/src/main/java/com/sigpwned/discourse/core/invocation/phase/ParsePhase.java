package com.sigpwned.discourse.core.invocation.phase;

import com.sigpwned.discourse.core.command.Command;
import java.util.List;
import java.util.Map;

public interface ParsePhase {

  public <T> List<Map.Entry<String, String>> parse(Command<T> command, List<String> args);
}
