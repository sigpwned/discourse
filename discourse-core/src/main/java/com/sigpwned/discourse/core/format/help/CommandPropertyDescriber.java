package com.sigpwned.discourse.core.format.help;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface CommandPropertyDescriber {
  public Optional<List<HelpMessage>> describe(LeafCommandProperty property,
      InvocationContext context);
}
