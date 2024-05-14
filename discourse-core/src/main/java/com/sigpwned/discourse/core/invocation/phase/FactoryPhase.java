package com.sigpwned.discourse.core.invocation.phase;

import com.sigpwned.discourse.core.invocation.model.command.Command;
import java.util.Map;

public interface FactoryPhase {

  public <T> T create(Command<T> command, Map<String, Object> state);
}
