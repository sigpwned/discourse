package com.sigpwned.discourse.core.format.help;

import java.util.List;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Maybe;

public interface CommandPropertyDescriber {
  public Maybe<List<HelpMessage>> describe(LeafCommandProperty property,
      InvocationContext context);
}
