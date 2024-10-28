package com.sigpwned.discourse.core.format.help.compose.property;

import java.util.List;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class DefaultValueCommandPropertyHelpDocumentCompser
    extends CommandPropertyDescriptionVisitingCommandHelpDocumentComposerBase {
  public static final DefaultValueCommandPropertyHelpDocumentCompser INSTANCE =
      new DefaultValueCommandPropertyHelpDocumentCompser();

  @Override
  protected void visitCommandPropertyDescription(CommandPropertyDescription description, InvocationContext context) {
    PlannedCommandProperty property = description.getProperty();
    if (property.getDefaultValue().isPresent()) {
      description.getDescriptions()
          .add(new UserMessage("If no value is given, then the default value is \"{0}\".",
              List.of(property.getDefaultValue().orElseThrow())));
    }
  }
}
