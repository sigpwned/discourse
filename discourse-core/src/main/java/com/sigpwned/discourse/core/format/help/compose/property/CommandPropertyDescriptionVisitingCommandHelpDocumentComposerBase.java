package com.sigpwned.discourse.core.format.help.compose.property;

import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.format.help.CommandHelpDocumentComposer;
import com.sigpwned.discourse.core.format.help.group.property.CommandPropertyGroupBlock;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public abstract class CommandPropertyDescriptionVisitingCommandHelpDocumentComposerBase
    implements CommandHelpDocumentComposer {

  @Override
  public void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context) {
    for (DocumentSection section : document.getSections()) {
      for (Block block : section.getBlocks()) {
        if (block instanceof CommandPropertyGroupBlock group) {
          for (CommandPropertyDescription description : group.getDescriptions()) {
            visitCommandPropertyDescription(description, context);
          }
        }
      }
    }
  }

  protected abstract void visitCommandPropertyDescription(CommandPropertyDescription description,
      InvocationContext context);

}
