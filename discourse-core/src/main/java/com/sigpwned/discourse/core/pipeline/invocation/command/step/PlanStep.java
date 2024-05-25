package com.sigpwned.discourse.core.pipeline.invocation.command.step;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.command.PlannedCommandProperty;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.module.value.sink.ValueSink;
import com.sigpwned.discourse.core.module.value.sink.ValueSinkFactory;

public class PlanStep {
  public <T> PlannedCommand<T> plan(ValueSinkFactory sinkFactory,
      ValueDeserializerFactory<?> deserializerFactory, ResolvedCommand<T> resolvedCommand) {
    if (!(resolvedCommand.getCommand() instanceof LeafCommand<T> leaf)) {
      // TODO better exception
      throw new IllegalArgumentException("not a leaf command");
    }

    List<PlannedCommandProperty> properties = new ArrayList<>(leaf.getProperties().size());
    for (LeafCommandProperty property : leaf.getProperties()) {
      ValueSink sink = sinkFactory.getSink(property.getGenericType(), property.getAnnotations())
          .orElseThrow(() -> {
            // TODO better exception
            return new IllegalArgumentException("no sink for " + property);
          });
      ValueDeserializer<?> deserializer = deserializerFactory
          .getDeserializer(sink.getGenericType(), property.getAnnotations()).orElseThrow(() -> {
            // TODO better exception
            return new IllegalArgumentException("no deserializer for " + property);
          });
      properties.add(new PlannedCommandProperty(property.getName(),
          property.getDescription().orElse(null), property.getCoordinates(), sink, deserializer));
    }

    return new PlannedCommand<>(resolvedCommand.getParents(),
        resolvedCommand.getName().orElse(null), resolvedCommand.getVersion().orElse(null),
        leaf.getDescription().orElse(null), properties, leaf.getConstructor());
  }
}
