package com.sigpwned.discourse.core.format.help.compose.property;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.l11n.UserMessageLocalizer;
import com.sigpwned.discourse.core.l11n.util.UserMessages;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class ExampleCommandPropertyHelpDocumentCompser
    extends CommandPropertyDescriptionVisitingCommandHelpDocumentComposerBase {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ExampleCommandPropertyHelpDocumentCompser.class);

  public static final ExampleCommandPropertyHelpDocumentCompser INSTANCE =
      new ExampleCommandPropertyHelpDocumentCompser();

  @Override
  protected void visitCommandPropertyDescription(CommandPropertyDescription description,
      InvocationContext context) {
    PlannedCommandProperty property = description.getProperty();

    UserMessageLocalizer localizer = context.get(UserMessageLocalizer.class).orElseThrow();

    UserMessage name = property.getDeserializer().name().orElse(null);
    if (name == null) {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No type name for property {}", property.getName());
      return;
    }
    name = localizer.localizeUserMessage(name, context);

    UserMessage example = property.getDeserializer().example().orElse(null);
    if (example == null) {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No example for property {}", property.getName());
      return;
    }
    example = localizer.localizeUserMessage(example, context);

    // TODO Should we allow an example using @DiscourseExampleValue or similar?
    description.getDescriptions().add(UserMessage.of("This property expects a {} value, e.g., {}",
        List.of(UserMessages.render(name), UserMessages.render(example))));
  }
}
