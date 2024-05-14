package com.sigpwned.discourse.core.invocation.phase.scan;

import com.sigpwned.discourse.core.command.RootCommand;

public interface ScanPhase {

  public <T> RootCommand<T> scan(Class<T> clazz);
}
