package com.sigpwned.discourse.core.invocation;

import com.sigpwned.discourse.core.invocation.phase.scan.RootCommand;

public interface ScanPhase {

  public <T> RootCommand<T> scan(Class<T> clazz);
}
