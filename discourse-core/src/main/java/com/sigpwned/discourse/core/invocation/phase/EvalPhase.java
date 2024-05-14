package com.sigpwned.discourse.core.invocation.phase;

import com.sigpwned.discourse.core.command.Command;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface EvalPhase {

  public <T> Map<String, Object> eval(Command<T> command, List<Entry<String, String>> state);
}
