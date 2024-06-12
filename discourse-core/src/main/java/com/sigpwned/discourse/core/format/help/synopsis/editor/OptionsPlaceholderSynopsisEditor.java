package com.sigpwned.discourse.core.format.help.synopsis.editor;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.format.help.SynopsisEditor;
import com.sigpwned.discourse.core.format.help.synopsis.entry.CommandNameSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.DiscriminatorSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.OptionsPlaceholderSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class OptionsPlaceholderSynopsisEditor implements SynopsisEditor {
  @Override
  public List<SynopsisEntry> editEntries(PlannedCommand<?> command, List<SynopsisEntry> entries,
      InvocationContext context) {
    if (command.getProperties().stream().flatMap(p -> p.getCoordinates().stream())
        .noneMatch(OptionCoordinate.class::isInstance)) {
      return entries;
    }

    int index = IntStream.range(0, entries.size())
        .dropWhile(i -> entries.get(i) instanceof CommandNameSynopsisEntry
            || entries.get(i) instanceof DiscriminatorSynopsisEntry)
        .findFirst().orElse(entries.size());

    List<SynopsisEntry> prefix = entries.subList(0, index);
    List<SynopsisEntry> suffix = entries.subList(index, entries.size());

    // TODO localize
    List<SynopsisEntry> result = new ArrayList<>(entries.size() + 1);
    result.addAll(prefix);
    result.add(OptionsPlaceholderSynopsisEntry.INSTANCE);
    result.addAll(suffix);

    return unmodifiableList(result);
  }
}
