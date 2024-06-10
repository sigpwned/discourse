package com.sigpwned.discourse.core.format.help.synopsis.editor;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.format.help.SynopsisEditor;
import com.sigpwned.discourse.core.format.help.synopsis.entry.LiteralSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.id.CommandNameSynopsisEntryId;
import com.sigpwned.discourse.core.format.help.synopsis.entry.id.DiscriminatorSynopsisEntryId;
import com.sigpwned.discourse.core.format.help.synopsis.entry.id.OptionsPlaceholderSynopsisEntryId;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class OptionsPlaceholderSynopsisEditor implements SynopsisEditor {
  @Override
  public List<SynopsisEntry> editEntries(ResolvedCommand<?> command, List<SynopsisEntry> entries,
      InvocationContext context) {
    if (command.getCommand().getProperties().stream().flatMap(p -> p.getCoordinates().stream())
        .noneMatch(OptionCoordinate.class::isInstance)) {
      return entries;
    }

    int index = IntStream.range(0, entries.size())
        .takeWhile(i -> entries.get(i).getId() instanceof CommandNameSynopsisEntryId
            || entries.get(i).getId() instanceof DiscriminatorSynopsisEntryId)
        .findFirst().orElse(-1);
    if (index == -1) {
      return entries;
    }

    List<SynopsisEntry> prefix = entries.subList(0, index + 1);
    List<SynopsisEntry> suffix = entries.subList(index + 1, entries.size());

    // TODO localize
    List<SynopsisEntry> result = new ArrayList<>(entries.size() + 1);
    result.addAll(prefix);
    result.add(new LiteralSynopsisEntry(OptionsPlaceholderSynopsisEntryId.INSTANCE, "[options]"));
    result.addAll(suffix);

    return unmodifiableList(result);
  }
}
