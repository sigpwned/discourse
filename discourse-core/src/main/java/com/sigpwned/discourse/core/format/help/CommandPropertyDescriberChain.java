package com.sigpwned.discourse.core.format.help;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Maybe;

public class CommandPropertyDescriberChain extends Chain<CommandPropertyDescriber>
    implements CommandPropertyDescriber {
  public static final CommandPropertyDescriberChain INSTANCE = new CommandPropertyDescriberChain();

  @Override
  public Maybe<List<HelpMessage>> describe(LeafCommandProperty property,
      InvocationContext context) {
    List<HelpMessage> descriptions = new ArrayList<>();

    for (CommandPropertyDescriber describer : this) {
      Maybe<List<HelpMessage>> maybeDescription = describer.describe(property, context);
      if (maybeDescription.isYes())
        descriptions.addAll(maybeDescription.orElseThrow());
      if (maybeDescription.isNo())
        return Maybe.no();
    }

    return descriptions.isEmpty() ? Maybe.maybe() : Maybe.yes(unmodifiableList(descriptions));
  }
}
