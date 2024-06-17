package com.sigpwned.discourse.core.format.help.render.text;

import static java.util.stream.Collectors.joining;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.IntStream;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.document.Block;
import com.sigpwned.discourse.core.document.Document;
import com.sigpwned.discourse.core.document.render.BlockRenderer;
import com.sigpwned.discourse.core.document.render.DocumentSection;
import com.sigpwned.discourse.core.document.render.text.TextViewport;
import com.sigpwned.discourse.core.format.help.SynopsisEntryFormatter;
import com.sigpwned.discourse.core.format.help.model.CommandSynopsis;
import com.sigpwned.discourse.core.format.help.model.synopsis.CommandNameCommandSynopsisEntry;
import com.sigpwned.discourse.core.format.help.model.synopsis.DiscriminatorCommandSynopsisEntry;
import com.sigpwned.discourse.core.format.help.model.synopsis.CommandSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.CommandSynopsisBlock;
import com.sigpwned.discourse.core.l11n.UserMessageLocalizer;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Text;

public class CommandSynopsisBlockTextRenderer implements BlockRenderer {

  @Override
  public boolean renderBlock(Document document, DocumentSection section, Block block,
      PrintStream out, InvocationContext context) throws IOException {
    if (!(block instanceof CommandSynopsisBlock synopsisBlock))
      return false;

    CommandSynopsis synopsis = synopsisBlock.getSynopsis();

    // TODO Is this where this should come from?
    PlannedCommand<?> command = context.get(PlannedCommand.class).orElseThrow();

    TextViewport viewport = context.get(TextViewport.class).orElse(TextViewport.DEFAULT);

    SynopsisEntryFormatter formatter = context.get(SynopsisEntryFormatter.class).orElseThrow();

    UserMessageLocalizer localizer = context.get(UserMessageLocalizer.class).orElseThrow();

    Dialect dialect = context.get(Dialect.class).orElseThrow();

    int index = IntStream.range(0, synopsis.getEntries().size())
        .dropWhile(i -> synopsis.getEntries().get(i) instanceof CommandNameCommandSynopsisEntry
            || synopsis.getEntries().get(i) instanceof DiscriminatorCommandSynopsisEntry)
        .findFirst().orElse(synopsis.getEntries().size());

    List<CommandSynopsisEntry> head = synopsis.getEntries().subList(0, index);
    List<CommandSynopsisEntry> tail = synopsis.getEntries().subList(index, synopsis.getEntries().size());

    String headText = head.stream()
        .map(e -> formatter.formatSynopsisEntry(command, e, context).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("Unable to format synopsis entry: " + e);
        })).collect(joining(" "));


    String tailText = tail.stream()
        .map(e -> formatter.formatSynopsisEntry(command, e, context).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("Unable to format synopsis entry: " + e);
        })).collect(joining(" "));

    int headWidth = headText.length();
    int tailWidth = viewport.getWidth() - headWidth - 1;

    List<String> tailLines = Text.wrap(tailText, tailWidth);

    StringBuilder result = new StringBuilder();
    result.append(headText).append(" ").append(tailLines.get(0)).append(System.lineSeparator());
    for (int i = 1; i < tailLines.size(); i++)
      result.append(Text.indent(tailLines.get(i), headWidth + 1)).append(System.lineSeparator());
    out.print(result.toString());
    out.flush();

    return true;
  }

}
