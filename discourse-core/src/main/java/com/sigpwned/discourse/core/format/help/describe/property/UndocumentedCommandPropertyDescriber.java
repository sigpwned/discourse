package com.sigpwned.discourse.core.format.help.describe.property;

import java.util.List;
import com.sigpwned.discourse.core.annotation.Undocumented;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.format.help.CommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.HelpMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Maybe;

public class UndocumentedCommandPropertyDescriber implements CommandPropertyDescriber {
  public static final UndocumentedCommandPropertyDescriber INSTANCE =
      new UndocumentedCommandPropertyDescriber();

  @Override
  public Maybe<List<HelpMessage>> describe(LeafCommandProperty property,
      InvocationContext context) {
    if (property.getAnnotations().stream().anyMatch(a -> a instanceof Undocumented))
      return Maybe.no();
    return Maybe.maybe();
  }
}
