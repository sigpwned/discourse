package com.sigpwned.discourse.core.format.help;

import java.util.Optional;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public interface CommandPropertySyntaxFormatter {
  public Optional<CommandPropertySyntax> formatParameterSyntax(LeafCommandProperty property,
      InvocationContext context);
}
