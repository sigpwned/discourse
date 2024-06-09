package com.sigpwned.discourse.core.format.help;

import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class CommandPropertySyntaxFormatterChain extends Chain<CommandPropertySyntaxFormatter>
    implements CommandPropertySyntaxFormatter {

  @Override
  public Optional<CommandPropertySyntax> formatParameterSyntax(LeafCommandProperty property,
      InvocationContext context) {
    for (CommandPropertySyntaxFormatter formatter : this) {
      Optional<CommandPropertySyntax> result = formatter.formatParameterSyntax(property, context);
      if (result.isPresent())
        return result;
    }
    return Optional.empty();
  }
}
