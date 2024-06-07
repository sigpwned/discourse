package com.sigpwned.discourse.core.format.help;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class CommandPropertySyntaxFormatterChain extends Chain<CommandPropertySyntaxFormatter>
    implements CommandPropertySyntaxFormatter {

  @Override
  public Optional<List<String>> formatParameterSyntax(LeafCommandProperty property,
      InvocationContext context) {
    for (CommandPropertySyntaxFormatter formatter : this) {
      Optional<List<String>> result = formatter.formatParameterSyntax(property, context);
      if (result.isPresent())
        return result;
    }
    return Optional.empty();
  }
}
