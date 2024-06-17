package com.sigpwned.discourse.core.format.help.group.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.block.SubheaderBlock;
import com.sigpwned.discourse.core.document.node.TextNode;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.format.help.CommandHelpDocumentComposer;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public abstract class CommandPropertyGroupCommandHelpDocumentComposerBase
    implements CommandHelpDocumentComposer {
  @Override
  public void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context) {
    List<PlannedCommandProperty> properties = new ArrayList<>();
    for (PlannedCommandProperty property : command.getProperties())
      if (filterProperty(property))
        properties.add(property);
    if (properties.isEmpty())
      return;

    CommandPropertyGroupBlock group = null;
    for (DocumentSection section : document.getSections()) {
      Optional<CommandPropertyGroupBlock> maybeGroup = findCommandPropertyGroupBlock(section);
      if (maybeGroup.isPresent()) {
        group = maybeGroup.orElseThrow();
        break;
      }
    }
    if (group == null) {
      group = newCommandPropertyGroupBlock();
      DocumentSection section = newDocumentSection(group);
      document.getSections().add(section);
    }

    for (PlannedCommandProperty property : properties) {
      CommandPropertyDescription description = newPropertyDescription(property);
      group.getDescriptions().add(description);
    }
  }

  /**
   * Filters the given {@link PlannedCommandProperty property} to determine if it should be included
   * in the {@link CommandPropertyGroupBlock group} being created.
   * 
   * @param property
   * @return
   */
  protected abstract boolean filterProperty(PlannedCommandProperty property);

  /**
   * Finds an existing desired {@link CommandPropertyGroupBlock group} in the given
   * {@link DocumentSection section}.
   * 
   * @param section
   * @return
   */
  protected Optional<CommandPropertyGroupBlock> findCommandPropertyGroupBlock(
      DocumentSection section) {
    return Optional.empty();
  }

  /**
   * Creates a new {@link DocumentSection section} for this group. Only called if a group does not
   * already exist in the document.
   * 
   * @return
   */
  protected DocumentSection newDocumentSection(CommandPropertyGroupBlock group) {
    return new DocumentSection(
        List.of(new SubheaderBlock(new TextNode(UserMessage.of("Properties"))), group));
  }

  /**
   * Creates a new empty {@link CommandPropertyGroupBlock group} for this command.
   * 
   * @return
   */
  protected CommandPropertyGroupBlock newCommandPropertyGroupBlock() {
    return new CommandPropertyGroupBlock();
  }

  /**
   * Creates a new {@link CommandPropertyDescription description} for the given
   * {@link PlannedCommandProperty property}.
   * 
   * @param property
   * @return
   */
  protected CommandPropertyDescription newPropertyDescription(PlannedCommandProperty property) {
    return new CommandPropertyDescription(property);
  }
}
