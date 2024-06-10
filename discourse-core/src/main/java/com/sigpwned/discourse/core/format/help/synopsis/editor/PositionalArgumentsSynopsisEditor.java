package com.sigpwned.discourse.core.format.help.synopsis.editor;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.format.help.SynopsisEditor;
import com.sigpwned.discourse.core.format.help.synopsis.entry.SynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.UserSynopsisEntry;
import com.sigpwned.discourse.core.format.help.synopsis.entry.id.CoordinateSynopsisEntryId;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSink;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSinkFactory;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.PlanStep;
import com.sigpwned.discourse.core.util.Streams;

public class PositionalArgumentsSynopsisEditor implements SynopsisEditor {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PositionalArgumentsSynopsisEditor.class);

  @Override
  public List<SynopsisEntry> editEntries(ResolvedCommand<?> command, List<SynopsisEntry> entries,
      InvocationContext context) {
    ValueSinkFactory sinkFactory = context.get(PlanStep.VALUE_SINK_FACTORY_KEY).orElseThrow();

    List<Map.Entry<Integer, LeafCommandProperty>> positionals = command.getCommand().getProperties()
        .stream()
        .filter(p -> p.getCoordinates().stream().anyMatch(PositionalCoordinate.class::isInstance))
        .map(p -> Map.entry(
            p.getCoordinates().stream().mapMulti(Streams.filterAndCast(PositionalCoordinate.class))
                .mapToInt(PositionalCoordinate::getPosition).min().orElseThrow(),
            p))
        .sorted(Comparator.comparing(Map.Entry::getKey)).collect(toList());

    List<SynopsisEntry> result = new ArrayList<>(entries);
    for (Map.Entry<Integer, LeafCommandProperty> positional : positionals) {
      Integer position = positional.getKey();
      LeafCommandProperty property = positional.getValue();

      boolean variadic;
      ValueSink sink =
          sinkFactory.getSink(property.getGenericType(), property.getAnnotations()).orElse(null);
      if (sink != null) {
        variadic = sink.isCollection();
      } else {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("No sink for property {} of type {}", property.getName(),
              property.getGenericType());
        }
        variadic = false;
      }

      result.add(
          new UserSynopsisEntry(CoordinateSynopsisEntryId.of(PositionalCoordinate.of(position)),
              property.isRequired(), variadic, property.getName()));
    }

    return unmodifiableList(result);
  }
}
