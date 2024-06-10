package com.sigpwned.discourse.core.format.help.describe.property;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.format.help.CommandPropertyDescriber;
import com.sigpwned.discourse.core.format.help.HelpMessage;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSink;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.PlanStep;
import com.sigpwned.discourse.core.util.Maybe;

public class ExampleCommandPropertyDescriber implements CommandPropertyDescriber {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ExampleCommandPropertyDescriber.class);

  public static final ExampleCommandPropertyDescriber INSTANCE =
      new ExampleCommandPropertyDescriber();

  @Override
  public Maybe<List<HelpMessage>> describe(LeafCommandProperty property,
      InvocationContext context) {
    String exampleValue = null;

    if (exampleValue == null && property.getExampleValue().isPresent())
      exampleValue = property.getExampleValue().orElseThrow();

    String expectedType = null;
    ValueSink sink = context.get(PlanStep.VALUE_SINK_FACTORY_KEY)
        .flatMap(factory -> factory.getSink(property.getGenericType(), property.getAnnotations()))
        .orElse(null);
    if (sink != null) {
      ValueDeserializer<?> deserializer = context.get(PlanStep.VALUE_DESERIALIZER_FACTORY_KEY)
          .flatMap(
              factory -> factory.getDeserializer(sink.getGenericType(), property.getAnnotations()))
          .orElse(null);
      if (deserializer != null) {
        if (expectedType == null && deserializer.name().isPresent())
          expectedType = deserializer.name().orElseThrow();
        if (deserializer.example().isPresent())
          exampleValue = deserializer.example().orElseThrow();
      } else {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("no deserializer for " + property.getGenericType() + " with "
              + property.getAnnotations() + " for " + property.getName() + " property");
      }
    } else {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("no sink for " + property.getGenericType() + " with "
            + property.getAnnotations() + " for " + property.getName() + " property");
    }

    if (expectedType == null)
      expectedType = property.getGenericType().getTypeName();

    if (exampleValue == null)
      return Maybe.maybe();

    // TODO i18n
    // TODO This impleemntation feels a little coupled...
    // TODO Should we allow an example using @DiscourseExampleValue or similar?
    return Maybe
        .yes(List.of(new HelpMessage("This property expects a value of type {0}, e.g., \"{1}\".",
            List.of(expectedType, exampleValue))));
  }
}
