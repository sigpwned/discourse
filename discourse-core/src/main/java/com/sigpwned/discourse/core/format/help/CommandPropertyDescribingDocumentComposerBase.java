package com.sigpwned.discourse.core.format.help;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.format.help.group.property.CommandPropertyGroupBlock;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public abstract class CommandPropertyDescribingDocumentComposerBase
    implements CommandHelpDocumentComposer {
  private final String id;
  private final int priority;

  public CommandPropertyDescribingDocumentComposerBase(String id, int priority) {
    this.id = requireNonNull(id);
    this.priority = priority;
    if (priority < 0)
      throw new IllegalArgumentException("priority must be non-negative");
  }

  @Override
  public final void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context) {
    List<CommandPropertyGroupBlock> blocks = new ArrayList<>();
    for (DocumentSection section : document.getSections()) {
      for (Block block : section.getBlocks()) {
        if (block instanceof CommandPropertyGroupBlock propertyBlock) {
          if (propertyBlock.getId().equals(getId())) {
            blocks.add(propertyBlock);
          }
        }
      }
    }

    if (blocks.isEmpty()) {
      DocumentSection section = new DocumentSection(new ArrayList<>());
      CommandPropertyGroupBlock block =
          new CommandPropertyGroupBlock(getId(), getPriority(), new ArrayList<>());
      section.getBlocks().add(block);
      document.getSections().add(section);
    }

    for (CommandPropertyGroupBlock block : blocks) {
      for (PlannedCommandProperty property : command.getProperties()) {
        Optional<List<UserMessage>> maybeDescription = describeCommandProperty(property, context);
        if (maybeDescription.isEmpty())
          continue;

        List<UserMessage> description = maybeDescription.get();
        CommandPropertyDescription x = block.getDescriptions().stream()
            .filter(d -> d.getProperty() == property).findFirst().orElse(null);
        if (x == null) {
          x = new CommandPropertyDescription(property, new ArrayList<>(), new ArrayList<>());
          block.getDescriptions().add(x);
        }

        x.getDescriptions().addAll(description);
      }
    }
  }

  public abstract Optional<List<UserMessage>> describeCommandProperty(
      PlannedCommandProperty property, InvocationContext context);

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the priority
   */
  public int getPriority() {
    return priority;
  }
}
