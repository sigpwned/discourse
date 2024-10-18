package com.sigpwned.discourse.core.format.help;

import java.util.Iterator;
import com.sigpwned.discourse.core.annotation.Undocumented;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.format.help.group.property.CommandPropertyGroupCommandHelpDocumentComposerBase;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyGroup;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Streams;

public class UndocumentedCommandHelpDocumentComposer implements CommandHelpDocumentComposer {
  public static final UserMessage OPTIONS_USER_MESSAGE =
      CommandPropertyGroupCommandHelpDocumentComposerBase.OPTIONS_USER_MESSAGE;

  public static final UserMessage VALUE_USER_MESSAGE =
      CommandPropertyGroupCommandHelpDocumentComposerBase.VALUE_USER_MESSAGE;

  @Override
  public void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context) {
    Iterator<DocumentSection> sectionsIterator = document.getSections().iterator();
    while (sectionsIterator.hasNext()) {
      DocumentSection section = sectionsIterator.next();
      if (section.getBlocks().stream().anyMatch(CommandPropertyGroup.class::isInstance)) {
        CommandPropertyGroup group = section.getBlocks().stream()
            .mapMulti(Streams.filterAndCast(CommandPropertyGroup.class)).findFirst().orElseThrow();

        Iterator<CommandPropertyDescription> descriptionsIterator =
            group.getDescriptions().iterator();
        while (descriptionsIterator.hasNext()) {
          CommandPropertyDescription description = descriptionsIterator.next();
          if (description.getProperty().getAnnotations().stream()
              .anyMatch(a -> a instanceof Undocumented)) {
            descriptionsIterator.remove();
          }
        }

        if (group.getDescriptions().isEmpty()) {
          sectionsIterator.remove();
        }
      }
    }
  }
}
