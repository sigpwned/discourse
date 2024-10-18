package com.sigpwned.discourse.core.format.help.group.property;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.document.block.SubheaderBlock;
import com.sigpwned.discourse.core.document.node.TextNode;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.util.Streams;

public abstract class OptionsCommandPropertyGroupCommandHelpDocumentComposer
    extends CommandPropertyGroupCommandHelpDocumentComposerBase {
  @Override
  protected boolean filterProperty(PlannedCommandProperty property) {
    return property.getCoordinates().stream().anyMatch(OptionCoordinate.class::isInstance);
  }

  @Override
  protected Optional<CommandPropertyGroupBlock> findCommandPropertyGroupBlock(
      DocumentSection section) {
    return section.getBlocks().stream()
        .mapMulti(Streams.filterAndCast(CommandPropertyGroupBlock.class)).filter(group -> group
            .getDescriptions().stream().allMatch(d -> filterProperty(d.getProperty())))
        .findFirst();
  }

  @Override
  protected DocumentSection newDocumentSection(CommandPropertyGroupBlock group) {
    return new DocumentSection(
        List.of(new SubheaderBlock(new TextNode(UserMessage.of("OPTIONS"))), group));
  }
}
