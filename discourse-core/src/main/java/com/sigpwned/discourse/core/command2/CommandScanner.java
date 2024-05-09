package com.sigpwned.discourse.core.command2;

public interface CommandScanner {

  public <T> RootCommand<T> scanForCommand(Class<T> clazz);
}
