package com.sigpwned.discourse.core.format.help.describe.property;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.format.help.CommandPropertyDescriber;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class DescriptionCommandPropertyDescriber implements CommandPropertyDescriber {
  public static final DescriptionCommandPropertyDescriber INSTANCE =
      new DescriptionCommandPropertyDescriber();

  @Override
  public Optional<List<String>> describe(LeafCommandProperty property, InvocationContext context) {
    return property.getDescription().map(List::of);
  }
}
