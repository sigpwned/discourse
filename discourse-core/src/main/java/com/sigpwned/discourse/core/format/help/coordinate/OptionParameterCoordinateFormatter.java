package com.sigpwned.discourse.core.format.help.coordinate;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.dialect.TokenFormatter;
import com.sigpwned.discourse.core.format.help.CommandPropertyCategory;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntax;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntaxFormatter;
import com.sigpwned.discourse.core.format.help.HelpMessage;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.core.plan.value.sink.ValueSink;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.PlanStep;
import com.sigpwned.discourse.core.util.Streams;

public class OptionParameterCoordinateFormatter implements CommandPropertySyntaxFormatter {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(OptionParameterCoordinateFormatter.class);

  public static final int CATEGORY_PRIORITY = 1000;

  public static final CommandPropertyCategory CATEGORY =
      new CommandPropertyCategory(CATEGORY_PRIORITY, HelpMessage.of("Options"));

  public static final OptionParameterCoordinateFormatter INSTANCE =
      new OptionParameterCoordinateFormatter();

  @Override
  public Optional<CommandPropertySyntax> formatParameterSyntax(LeafCommandProperty property,
      InvocationContext context) {
    Dialect dialect = context.get(InvocationPipelineStep.DIALECT_KEY).orElseThrow();

    ValueSink sink = context.get(PlanStep.VALUE_SINK_FACTORY_KEY)
        .flatMap(factory -> factory.getSink(property.getGenericType(), property.getAnnotations()))
        .orElse(null);
    if (sink == null) {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("no sink for " + property.getGenericType() + " with "
            + property.getAnnotations() + " for " + property.getName() + " property");
    }

    ValueDeserializer<?> deserializer = context.get(PlanStep.VALUE_DESERIALIZER_FACTORY_KEY)
        .flatMap(
            factory -> factory.getDeserializer(sink.getGenericType(), property.getAnnotations()))
        .orElse(null);
    if (deserializer == null) {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("no deserializer for " + property.getGenericType() + " with "
            + property.getAnnotations() + " for " + property.getName() + " property");
    }

    TokenFormatter tokenFormatter = dialect.newTokenFormatter();

    List<OptionCoordinate> options = property.getCoordinates().stream()
        .mapMulti(Streams.filterAndCast(OptionCoordinate.class)).toList();
    if (options.isEmpty())
      return Optional.empty();

    List<String> result = new ArrayList<>();
    for (OptionCoordinate option : options) {
      String optionSyntax =
          tokenFormatter.formatToken(new SwitchNameToken(option.getName(), false)).orElseThrow();

      // TODO localize?
      String valueSyntax = null;
      if (valueSyntax == null && deserializer != null)
        valueSyntax = "<" + deserializer.name().orElse(null) + ">";
      if (valueSyntax == null) {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("no value syntax for " + property.getName() + " property, using default...");
        valueSyntax = "<value>";
      }

      result.add(optionSyntax + " " + valueSyntax);
    }

    SwitchName key = options.stream().map(OptionCoordinate::getName).min(Comparator.naturalOrder())
        .orElseThrow();

    return Optional.of(new CommandPropertySyntax(CATEGORY, key, unmodifiableList(result)));
  }
}
