package com.sigpwned.discourse.core.format.help.describe.property;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.format.help.CommandPropertyDescriber;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class RequiredCommandPropertyDescriber implements CommandPropertyDescriber {
  public static final RequiredCommandPropertyDescriber INSTANCE =
      new RequiredCommandPropertyDescriber();

  @Override
  public Optional<List<String>> describe(LeafCommandProperty property, InvocationContext context) {
    if (!property.isRequired())
      return Optional.empty();
    return Optional.of(List.of("This property is required."));
  }
}
