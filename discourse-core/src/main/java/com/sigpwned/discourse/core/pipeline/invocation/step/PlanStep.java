package com.sigpwned.discourse.core.pipeline.invocation.step;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.command.planned.ParentCommand;
import com.sigpwned.discourse.core.command.planned.PlannedCommand;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.command.resolved.ResolvedCommand;
import com.sigpwned.discourse.core.command.tree.LeafCommand;
import com.sigpwned.discourse.core.command.tree.LeafCommandProperty;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSink;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSinkFactory;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception.InvalidDefaultValuePlanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception.InvalidExampleValuePlanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception.NoDeserializerAvailablePlanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.plan.exception.NoSinkAvailablePlanException;

public class PlanStep extends InvocationPipelineStepBase {
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static final InvocationContext.Key<ValueDeserializerFactory<?>> VALUE_DESERIALIZER_FACTORY_KEY =
      (InvocationContext.Key) InvocationContext.Key.of(ValueDeserializerFactory.class);

  public static final InvocationContext.Key<ValueSinkFactory> VALUE_SINK_FACTORY_KEY =
      InvocationContext.Key.of(ValueSinkFactory.class);

  public <T> PlannedCommand<T> plan(ResolvedCommand<T> resolvedCommand, InvocationContext context) {
    ValueSinkFactory sinkFactory = context.get(VALUE_SINK_FACTORY_KEY).orElseThrow();

    ValueDeserializerFactory<?> deserializerFactory =
        context.get(VALUE_DESERIALIZER_FACTORY_KEY).orElseThrow();

    PlannedCommand<T> plannedCommand;
    try {
      ResolvedCommand<T> mutableResolvedCommand = mutableCopyOf(resolvedCommand);

      getListener(context).beforePlanStep(mutableResolvedCommand, context);

      ResolvedCommand<T> immutableResolvedCommand = immutableCopyOf(mutableResolvedCommand);

      plannedCommand = doPlan(immutableResolvedCommand.getCommand(), sinkFactory,
          deserializerFactory, resolvedCommand, context);

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

      Object defaultValue;
      if (property.getDefaultValue().isPresent()) {
        String defaultValueString = property.getDefaultValue().orElseThrow();
        try {
          defaultValue = deserializer.deserialize(defaultValueString);
        } catch (Exception e) {
          throw new InvalidDefaultValuePlanException(leaf, property.getName(), defaultValueString,
              e);
        }
      } else {
        defaultValue = null;
      }

      Object exampleValue;
      if (property.getExampleValue().isPresent()) {
        String exampleValueString = property.getExampleValue().orElseThrow();
        try {
          exampleValue = deserializer.deserialize(exampleValueString);
        } catch (Exception e) {
          throw new InvalidExampleValuePlanException(leaf, property.getName(), exampleValueString,
              e);
        }
      } else {
        exampleValue = null;
      }

      properties.add(new PlannedCommandProperty(property.getName(),
          property.getDescription().orElse(null), property.isRequired(), defaultValue, exampleValue,
          property.getAnnotations(), property.getCoordinates(), sink, deserializer));
    }

    return new PlannedCommand<>(resolvedCommand.getParents(),
        resolvedCommand.getName().orElse(null), resolvedCommand.getVersion().orElse(null),
        leaf.getDescription().orElse(null), properties, leaf.getReactor(), leaf.getConstructor());
  }

  protected <T> ResolvedCommand<T> mutableCopyOf(ResolvedCommand<T> originalResolvedCommand) {
    LeafCommand<T> originalLeafCommand = originalResolvedCommand.getCommand();

    LeafCommand<T> mutableLeafCommand =
        new LeafCommand<T>(originalLeafCommand.getDescription().orElse(null),
            new ArrayList<>(originalLeafCommand.getProperties()), originalLeafCommand.getReactor(),
            originalLeafCommand.getConstructor());
    List<ParentCommand> mutableParents = new ArrayList<>(originalResolvedCommand.getParents());

    return new ResolvedCommand<T>(originalResolvedCommand.getName().orElse(null),
        originalResolvedCommand.getVersion().orElse(null), mutableParents, mutableLeafCommand);
  }

  protected <T> ResolvedCommand<T> immutableCopyOf(ResolvedCommand<T> originalResolvedCommand) {
    LeafCommand<T> originalLeafCommand = originalResolvedCommand.getCommand();

    LeafCommand<T> immutableLeafCommand =
        new LeafCommand<T>(originalLeafCommand.getDescription().orElse(null),
            List.copyOf(originalLeafCommand.getProperties()), originalLeafCommand.getReactor(),
            originalLeafCommand.getConstructor());
    List<ParentCommand> immutableParents = List.copyOf(originalResolvedCommand.getParents());

    return new ResolvedCommand<T>(originalResolvedCommand.getName().orElse(null),
        originalResolvedCommand.getVersion().orElse(null), immutableParents, immutableLeafCommand);
  }
}
