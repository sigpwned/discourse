package com.sigpwned.discourse.core.invocation.phase.scan;

public interface CommandScanner {

  public <T> RootCommand<T> scanForCommand(Class<T> clazz);
}
