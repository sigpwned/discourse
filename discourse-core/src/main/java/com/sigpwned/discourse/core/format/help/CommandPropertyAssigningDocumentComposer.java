package com.sigpwned.discourse.core.format.help;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.dialect.ArgFormatter;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.format.help.group.property.CommandPropertyGroupBlock;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyAssignment;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class CommandPropertyAssigningDocumentComposer implements CommandHelpDocumentComposer {

  @Override
  public final void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context) {
    Dialect dialect = context.get(Dialect.class).orElseThrow();

    ArgFormatter formatter = dialect.newArgFormatter();

    List<CommandPropertyGroupBlock> blocks = new ArrayList<>();

    for (DocumentSection section : document.getSections()) {
      for (Block block : section.getBlocks()) {
        if (block instanceof CommandPropertyGroupBlock groupBlock) {
          blocks.add(groupBlock);
          for (CommandPropertyDescription description : groupBlock.getDescriptions()) {
            for (Coordinate coordinate : description.getProperty().getCoordinates()) {
              Optional<List<List<String>>> maybeAssignments =
                  formatter.formatArg(description.getProperty(), coordinate);
              if (maybeAssignments.isPresent()) {
                List<List<String>> assignments = maybeAssignments.orElseThrow();
                for (List<String> assignment : assignments) {
                  description.getAssignments().add(new CommandPropertyAssignment(coordinate,
                      new UserMessage(String.join(" ", assignment))));
                }
              }
            }
          }
        }
      }
    }
  }
}
