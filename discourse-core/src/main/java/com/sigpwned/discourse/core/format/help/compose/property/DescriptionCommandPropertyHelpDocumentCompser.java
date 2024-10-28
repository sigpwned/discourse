package com.sigpwned.discourse.core.format.help.compose.property;

import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class DescriptionCommandPropertyHelpDocumentCompser
    extends CommandPropertyDescriptionVisitingCommandHelpDocumentComposerBase {
  public static final DescriptionCommandPropertyHelpDocumentCompser INSTANCE =
      new DescriptionCommandPropertyHelpDocumentCompser();

  @Override
  protected void visitCommandPropertyDescription(CommandPropertyDescription description, InvocationContext context) {
    PlannedCommandProperty property = description.getProperty();
    if (property.getDescription().isPresent()) {
      description.getDescriptions().add(UserMessage.of(property.getDescription().orElseThrow()));
    }
  }
}
