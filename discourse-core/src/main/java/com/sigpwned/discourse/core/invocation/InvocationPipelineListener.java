package com.sigpwned.discourse.core.invocation;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.invocation.model.CommandDereference;
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
      List<Map.Entry<String, String>> parsedArgs);

  public <T> void beforeEval(Command<T> resolvedCommand,
      List<Map.Entry<String, String>> parsedArgs);

  public <T> void afterEval(Command<T> resolvedCommand, List<Map.Entry<String, String>> parsedArgs,
      Map<String, Object> initialState);

  public <T> void beforeFactory(Command<T> resolvedCommand, Map<String, Object> initialState);

  public <T> void afterFactory(Command<T> resolvedCommand, Map<String, Object> initialState,
      T instance);
}
