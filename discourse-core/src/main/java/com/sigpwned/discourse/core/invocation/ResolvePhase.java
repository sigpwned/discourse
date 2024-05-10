package com.sigpwned.discourse.core.invocation;

import com.sigpwned.discourse.core.invocation.phase.resolve.model.ResolvedCommand;
import com.sigpwned.discourse.core.invocation.phase.scan.RootCommand;
import java.util.List;

public interface ResolvePhase {
  public <T> ResolvedCommand<? extends T> resolveCommand(RootCommand<T> rootCommand, List<String> args);
}
