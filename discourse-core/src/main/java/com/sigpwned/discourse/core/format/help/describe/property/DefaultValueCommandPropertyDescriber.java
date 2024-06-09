package com.sigpwned.discourse.core.format.help.describe.property;

import java.util.List;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.format.help.CommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.HelpMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Maybe;

public class DefaultValueCommandPropertyDescriber implements CommandPropertyDescriber {
  public static final DefaultValueCommandPropertyDescriber INSTANCE =
      new DefaultValueCommandPropertyDescriber();

  @Override
  public Maybe<List<HelpMessage>> describe(LeafCommandProperty property,
      InvocationContext context) {
    if (property.getDefaultValue().isEmpty())
      return Maybe.maybe();
    // TODO i18n
    return Maybe
        .yes(List.of(new HelpMessage("If no value is given, then the default value is \"{0}\".",
            List.of(property.getDefaultValue().orElseThrow()))));
  }
}
