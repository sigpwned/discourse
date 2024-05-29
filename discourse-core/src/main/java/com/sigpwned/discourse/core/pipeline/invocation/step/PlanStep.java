package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.command.LeafCommand;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.command.PlannedCommand;
import com.sigpwned.discourse.core.command.PlannedCommandProperty;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSink;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSinkFactory;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception.NoDeserializerAvailablePlanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception.NoSinkAvailablePlanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception.NonLeafCommandPlanException;

public class PlanStep extends InvocationPipelineStepBase {
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static final InvocationContext.Key<ValueDeserializerFactory<?>> VALUE_DESERIALIZER_FACTORY_KEY =
      (InvocationContext.Key) InvocationContext.Key.of(ValueDeserializerFactory.class);

  public static final InvocationContext.Key<ValueSinkFactory> VALUE_SINK_FACTORY_KEY =
      InvocationContext.Key.of(ValueSinkFactory.class);

  public <T> PlannedCommand<T> plan(ResolvedCommand<T> resolvedCommand, InvocationContext context) {
    if (!(resolvedCommand.getCommand() instanceof LeafCommand<T> leaf)) {
      // TODO better exception
      throw new NonLeafCommandPlanException(resolvedCommand.getCommand());
    }

    ValueSinkFactory sinkFactory = context.get(VALUE_SINK_FACTORY_KEY).orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("no sink factory");
    });

    ValueDeserializerFactory<?> deserializerFactory =
        context.get(VALUE_DESERIALIZER_FACTORY_KEY).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("no deserializer factory");
        });

    PlannedCommand<T> plannedCommand;
    try {
      getListener(context).beforePlanStep(resolvedCommand, context);
      plannedCommand = doPlan(leaf, sinkFactory, deserializerFactory, resolvedCommand, context);
      getListener(context).afterPlanStep(resolvedCommand, plannedCommand, context);
    } catch (Throwable e) {
      getListener(context).catchPlanStep(e, context);
      throw e;
    } finally {
      getListener(context).finallyPlanStep(context);
    }

    return plannedCommand;
  }

  protected <T> PlannedCommand<T> doPlan(LeafCommand<T> leaf, ValueSinkFactory sinkFactory,
      ValueDeserializerFactory<?> deserializerFactory, ResolvedCommand<T> resolvedCommand,
      InvocationContext context) {
    List<PlannedCommandProperty> properties = new ArrayList<>(leaf.getProperties().size());

    for (LeafCommandProperty property : leaf.getProperties()) {
      ValueSink sink = sinkFactory.getSink(property.getGenericType(), property.getAnnotations())
          .orElseThrow(() -> {
            return new NoSinkAvailablePlanException(leaf, property);
          });
      ValueDeserializer<?> deserializer = deserializerFactory
          .getDeserializer(sink.getGenericType(), property.getAnnotations()).orElseThrow(() -> {
            return new NoDeserializerAvailablePlanException(leaf, property);
          });
      properties.add(new PlannedCommandProperty(property.getName(),
          property.getDescription().orElse(null), property.getCoordinates(), sink, deserializer));
    }

    return new PlannedCommand<>(resolvedCommand.getParents(),
        resolvedCommand.getName().orElse(null), resolvedCommand.getVersion().orElse(null),
        leaf.getDescription().orElse(null), properties, leaf.getConstructor());
  }
}
