package com.sigpwned.discourse.core.format.help.synopsis.format;

import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.stream.IntStream;
import com.sigpwned.discourse.core.format.help.Synopsis;
import com.sigpwned.discourse.core.format.help.SynopsisFormatter;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.id.CommandNameSynopsisEntryId;
import com.sigpwned.discourse.core.format.help.synopsis.entry.id.DiscriminatorSynopsisEntryId;
import com.sigpwned.discourse.core.text.TableLayout;

public class DefaultSynopsisFormatter implements SynopsisFormatter {

  @Override
  public String formatSynopsis(Synopsis synopsis) {
    int index = IntStream.range(0, synopsis.getEntries().size())
        .dropWhile(i -> synopsis.getEntries().get(i).getId() instanceof CommandNameSynopsisEntryId
            || synopsis.getEntries().get(i).getId() instanceof DiscriminatorSynopsisEntryId)
        .findFirst().orElse(-1);

    if (index < 1)
      return synopsis.getEntries().stream().map(SynopsisEntry::getText).collect(joining(" "));

    List<SynopsisEntry> commandEntries = synopsis.getEntries().subList(0, index);
    List<SynopsisEntry> argumentEntries =
        synopsis.getEntries().subList(index, synopsis.getEntries().size());

    String commandText = commandEntries.stream().map(SynopsisEntry::getText).collect(joining(" "));
    String argumentText =
        argumentEntries.stream().map(SynopsisEntry::getText).collect(joining(" "));

    TableLayout layout = new TableLayout(2);
    layout.addRow(new TableLayout.Row(List.of(commandText, argumentText)));
    return layout.toString(80,
        new int[] {TableLayout.COLUMN_WIDTH_TIGHT, TableLayout.COLUMN_WIDTH_FLEX}, 0, 1);
  }
}
