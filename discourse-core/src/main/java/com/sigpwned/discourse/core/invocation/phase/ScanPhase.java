package com.sigpwned.discourse.core.invocation.phase;

import com.sigpwned.discourse.core.invocation.model.command.RootCommand;

public interface ScanPhase {

  public <T> RootCommand<T> scan(Class<T> clazz);
}
