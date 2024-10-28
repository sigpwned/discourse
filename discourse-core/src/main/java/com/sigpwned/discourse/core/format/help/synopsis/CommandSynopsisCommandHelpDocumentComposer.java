package com.sigpwned.discourse.core.format.help.synopsis;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.block.SubheaderBlock;
import com.sigpwned.discourse.core.document.node.TextNode;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.format.help.CommandHelpDocumentComposer;
import com.sigpwned.discourse.core.format.help.CommandSynopsisComposer;
import com.sigpwned.discourse.core.format.help.model.CommandSynopsis;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class CommandSynopsisCommandHelpDocumentComposer implements CommandHelpDocumentComposer {

  @Override
  public void composeCommandHelpDocument(PlannedCommand<?> command, Document document,
      InvocationContext context) {
    CommandSynopsisComposer composer = context.get(CommandSynopsisComposer.class).orElseThrow();

    CommandSynopsis synopsis = new CommandSynopsis();
    composer.composeSynopsis(command, synopsis, context);

    CommandSynopsisBlock synopsisBlock = new CommandSynopsisBlock(synopsis);
    SubheaderBlock subheaderBlock = new SubheaderBlock(new TextNode(UserMessage.of("synopsis")));

    List<Block> blocks = new ArrayList<>();
    blocks.add(subheaderBlock);
    blocks.add(synopsisBlock);

    DocumentSection section = new DocumentSection(blocks);

    document.getSections().add(0, section);
  }
}
