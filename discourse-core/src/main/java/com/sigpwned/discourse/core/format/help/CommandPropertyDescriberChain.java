package com.sigpwned.discourse.core.format.help;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class CommandPropertyDescriberChain extends Chain<CommandPropertyDescriber>
    implements CommandPropertyDescriber {
  public static final CommandPropertyDescriberChain INSTANCE = new CommandPropertyDescriberChain();

  @Override
  public Optional<List<HelpMessage>> describe(LeafCommandProperty property,
      InvocationContext context) {
    List<HelpMessage> descriptions = new ArrayList<>();

    for (CommandPropertyDescriber describer : this) {
      Optional<List<HelpMessage>> maybeDescription = describer.describe(property, context);
      if (maybeDescription.isPresent())
        descriptions.addAll(maybeDescription.orElseThrow());
    }

    return descriptions.isEmpty() ? Optional.empty() : Optional.of(unmodifiableList(descriptions));
  }
}
