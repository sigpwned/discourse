package com.sigpwned.discourse.core.invocation.phase.scan;

import com.sigpwned.discourse.core.invocation.model.command.RootCommand;

public interface CommandScanner {

  public <T> RootCommand<T> scanForCommand(Class<T> clazz);
}
