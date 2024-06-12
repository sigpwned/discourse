package com.sigpwned.discourse.core.format.help.synopsis;

import static java.util.stream.Collectors.joining;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.IntStream;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.document.Doc;
import com.sigpwned.discourse.core.document.DocumentSection;
import com.sigpwned.discourse.core.document.DocumentSectionRenderer;
import com.sigpwned.discourse.core.document.render.text.TextViewport;
import com.sigpwned.discourse.core.format.help.Synopsis;
import com.sigpwned.discourse.core.format.help.synopsis.entry.CommandNameSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.DiscriminatorSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntryFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Text;

public class TextSynopsisDocumentSectionRenderer implements DocumentSectionRenderer {

  @Override
  public boolean renderDocumentSection(Doc document, DocumentSection section, PrintStream out,
      InvocationContext context) throws IOException {
    if (!(section instanceof SynopsisDocumentSection synopsisSection))
      return false;

    PlannedCommand<?> command = context.get(PlannedCommand.class).orElseThrow();

    SynopsisEntryFormatter formatter = context.get(SynopsisEntryFormatter.class).orElseThrow();

    TextViewport viewport = context.get(TextViewport.class).orElse(TextViewport.DEFAULT);

    Synopsis synopsis = synopsisSection.getSynopsis();

    int index = IntStream.range(0, synopsis.getEntries().size())
        .dropWhile(i -> synopsis.getEntries().get(i) instanceof CommandNameSynopsisEntry
            || synopsis.getEntries().get(i) instanceof DiscriminatorSynopsisEntry)
        .findFirst().orElse(synopsis.getEntries().size());

    List<SynopsisEntry> head = synopsis.getEntries().subList(0, index);
    List<SynopsisEntry> tail = synopsis.getEntries().subList(index, synopsis.getEntries().size());

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
