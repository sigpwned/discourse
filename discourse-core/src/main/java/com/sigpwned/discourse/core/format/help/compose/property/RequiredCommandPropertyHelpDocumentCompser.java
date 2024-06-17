package com.sigpwned.discourse.core.format.help.compose.property;

import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class RequiredCommandPropertyHelpDocumentCompser
    extends CommandPropertyDescriptionVisitingCommandHelpDocumentComposerBase {
  public static final RequiredCommandPropertyHelpDocumentCompser INSTANCE =
      new RequiredCommandPropertyHelpDocumentCompser();

  @Override
  protected void visitCommandPropertyDescription(CommandPropertyDescription description,
      InvocationContext context) {
    PlannedCommandProperty property = description.getProperty();
    if (property.isRequired()) {
      description.getDescriptions().add(UserMessage.of("This property is required."));
    }
  }
}
