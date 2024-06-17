package com.sigpwned.discourse.core.format.help;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.format.help.model.CommandSynopsis;
import com.sigpwned.discourse.core.format.help.synopsis.CommandSynopsisBlock;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class CommandSynopsisComposingDocumentComposer implements CommandHelpDocumentComposer {

  @Override
  public final void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context) {
    CommandSynopsisComposer composer = context.get(CommandSynopsisComposer.class).orElseThrow();

    List<CommandSynopsisBlock> blocks = new ArrayList<>();
    for (DocumentSection section : document.getSections()) {
      for (Block block : section.getBlocks()) {
        if (block instanceof CommandSynopsisBlock synopsisBlock) {
          blocks.add(synopsisBlock);
        }
      }
    }

    if (blocks.isEmpty()) {
      DocumentSection section = new DocumentSection(new ArrayList<>());
      CommandSynopsisBlock block = new CommandSynopsisBlock(new CommandSynopsis(new ArrayList<>()));
      blocks.add(block);
      section.getBlocks().add(block);
      document.getSections().add(section);
    }

    for (CommandSynopsisBlock block : blocks) {
      composer.composeSynopsis(command, block.getSynopsis(), context);
    }
  }
}
