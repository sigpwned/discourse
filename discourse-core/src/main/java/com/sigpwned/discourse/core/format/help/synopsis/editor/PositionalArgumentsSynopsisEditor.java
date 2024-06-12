package com.sigpwned.discourse.core.format.help.synopsis.editor;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.command.PlannedCommandProperty;
import com.sigpwned.discourse.core.format.help.SynopsisEditor;
import com.sigpwned.discourse.core.format.help.synopsis.entry.PositionalArgumentSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class PositionalArgumentsSynopsisEditor implements SynopsisEditor {
  @Override
  public List<SynopsisEntry> editEntries(PlannedCommand<?> command, List<SynopsisEntry> entries,
      InvocationContext context) {
    List<PositionalArgumentSynopsisEntry> positionalEntries = new ArrayList<>();
    for (PlannedCommandProperty property : command.getProperties()) {
      for (Coordinate coordinate : property.getCoordinates()) {
        if (coordinate instanceof PositionalCoordinate positionalCoordinate) {
          positionalEntries.add(new PositionalArgumentSynopsisEntry(positionalCoordinate));
        }
      }
    }

    positionalEntries.sort(Comparator.comparing(PositionalArgumentSynopsisEntry::getCoordinate));

    List<SynopsisEntry> result = new ArrayList<>(entries);
    result.addAll(positionalEntries);

    return unmodifiableList(result);
  }
}
