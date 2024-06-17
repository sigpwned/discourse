package com.sigpwned.discourse.core.format.help;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.format.help.group.property.CommandPropertyGroupBlock;
import com.sigpwned.discourse.core.format.help.model.CommandPropertyDescription;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Streams;

public abstract class CommandSynopsisComposingDocumentComposerBase
    implements CommandHelpDocumentComposer {
  @Override
  public final void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context) {
    DocumentSection section = document.getSections().stream()
        .filter(s -> s.getBlocks().stream()
            .mapMulti(Streams.filterAndCast(CommandPropertyGroupBlock.class))
            .anyMatch(b -> b.getId().equals(getId())))
        .findFirst().orElse(null);
    if (section == null) {
      section = new DocumentSection(new ArrayList<>());
      document.getSections().add(section);
    }

    CommandPropertyGroupBlock block = section.getBlocks().stream()
        .mapMulti(Streams.filterAndCast(CommandPropertyGroupBlock.class))
        .filter(b -> b.getId().equals(getId())).findFirst().orElse(null);
    if (block == null) {
      block = new CommandPropertyGroupBlock(getId(), getPriority(), new ArrayList<>());
      section.getBlocks().add(block);
    }

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
