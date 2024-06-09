package com.sigpwned.discourse.core.format.help.describe.property;

import java.util.List;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.format.help.CommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.HelpMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Maybe;

public class DescriptionCommandPropertyDescriber implements CommandPropertyDescriber {
  public static final DescriptionCommandPropertyDescriber INSTANCE =
      new DescriptionCommandPropertyDescriber();

  @Override
  public Maybe<List<HelpMessage>> describe(LeafCommandProperty property,
      InvocationContext context) {
    String description = property.getDescription().orElse(null);
    if (description == null)
      return Maybe.maybe();

    return Maybe.yes(List.of(HelpMessage.of(description)));
  }
}
