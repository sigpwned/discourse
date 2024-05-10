package com.sigpwned.discourse.core.invocation;

import com.sigpwned.discourse.core.invocation.phase.resolve.model.CommandDereference;
import com.sigpwned.discourse.core.invocation.phase.scan.Command;
import com.sigpwned.discourse.core.invocation.phase.scan.RootCommand;
import java.util.List;
import java.util.Map;

public interface InvocationPipelineListener {

  public <T> void beforeScan(Class<T> clazz);

  public <T> void afterScan(Class<T> clazz, RootCommand<T> rootCommand);

  public <T> void beforeResolve(RootCommand<T> rootCommand, List<String> args);

  public <T> void afterResolve(RootCommand<T> rootCommand, List<String> args,
      Command<? extends T> resolvedCommand,
      List<CommandDereference<? extends T>> commandDereferences, List<String> resolvedArgs);

  public <T> void beforeParse(Command<T> resolvedCommand, List<String> resolvedArgs);

  public <T> void afterParse(Command<T> resolvedCommand, List<String> resolvedArgs,
      Map<String, Object> initialState);

  public <T> void beforeFactory(Command<T> resolvedCommand, Map<String, Object> initialState);

  public <T> void afterFactory(Command<T> resolvedCommand, Map<String, Object> initialState,
      T instance);
}
