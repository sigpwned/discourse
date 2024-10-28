package com.sigpwned.discourse.core.format.help;

import static java.util.Collections.emptyList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.block.SubheaderBlock;
import com.sigpwned.discourse.core.document.node.TextNode;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.format.help.group.property.CommandPropertyGroupBlock;
import com.sigpwned.discourse.core.format.help.group.property.CommandPropertyGroupCommandHelpDocumentComposerBase;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyAssignment;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyGroup;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.module.parameter.flag.FlagCoordinate;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Streams;

public class FlagPropertiesCommandHelpDocumentComposer implements CommandHelpDocumentComposer {
  public static final UserMessage OPTIONS_USER_MESSAGE =
      CommandPropertyGroupCommandHelpDocumentComposerBase.OPTIONS_USER_MESSAGE;

  public static final UserMessage VALUE_USER_MESSAGE =
      CommandPropertyGroupCommandHelpDocumentComposerBase.VALUE_USER_MESSAGE;

  @Override
  public void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context) {
    List<PlannedCommandProperty> flags = command.getProperties().stream()
        .filter(p -> p.getCoordinates().stream().anyMatch(c -> c instanceof FlagCoordinate))
        .toList();

    if (flags.isEmpty())
      return;

    DocumentSection optionsSection = document.getSections().stream()
        .filter(s -> s.getBlocks().stream()
            .mapMulti(Streams.filterAndCast(CommandPropertyGroupBlock.class))
            .anyMatch(b -> b.getGroup().getId().equals(CommandPropertyGroup.OPTIONS_GROUP_ID)))
        .findFirst().orElse(null);
    if (optionsSection == null) {
      optionsSection =
          new DocumentSection(List.of(new SubheaderBlock(new TextNode(OPTIONS_USER_MESSAGE)),
              new CommandPropertyGroupBlock(
                  new CommandPropertyGroup(CommandPropertyGroup.OPTIONS_GROUP_ID,
                      CommandPropertyGroup.OPTIONS_GROUP_PRIORITY, new ArrayList<>()))));
      document.getSections().add(optionsSection);
    }

    CommandPropertyGroupBlock optionsBlock = optionsSection.getBlocks().stream()
        .mapMulti(Streams.filterAndCast(CommandPropertyGroupBlock.class)).findFirst().orElseThrow();

    CommandPropertyGroup optionsGroup = optionsBlock.getGroup();

    CommandPropertyDescriber describer = context.get(CommandPropertyDescriber.class).orElseThrow();

    List<CommandPropertyDescription> descriptions = optionsGroup.getDescriptions();
    for (PlannedCommandProperty flag : flags) {
      List<UserMessage> messages = describer.describe(flag, context).orElse(emptyList());
      List<CommandPropertyAssignment> syntaxes = new ArrayList<>();
      for (Coordinate coordinate : flag.getCoordinates()) {
        if (coordinate instanceof FlagCoordinate flagCoordinate) {
          syntaxes.add(new CommandPropertyAssignment(flagCoordinate, null));
        }
      }

      descriptions.add(new CommandPropertyDescription(flag, syntaxes, messages));
    }

    descriptions.sort(Comparator.naturalOrder());
  }
}
