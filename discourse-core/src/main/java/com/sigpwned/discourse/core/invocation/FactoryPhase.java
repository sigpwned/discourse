package com.sigpwned.discourse.core.invocation;

import com.sigpwned.discourse.core.invocation.phase.scan.Command;
import java.util.Map;

public interface FactoryPhase {

  public <T> T create(Command<T> command, Map<String, Object> state);
}
