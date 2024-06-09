package com.sigpwned.discourse.core.module.parameter.environmentvariable;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.format.help.CommandPropertyCategory;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntax;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntaxFormatter;
import com.sigpwned.discourse.core.format.help.HelpMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Streams;

public class EnvironmentVariableCoordinateFormatter implements CommandPropertySyntaxFormatter {
  public static final int CATEGORY_PRIORITY = 10000;

  public static final CommandPropertyCategory CATEGORY =
      new CommandPropertyCategory(CATEGORY_PRIORITY, HelpMessage.of("Environment Variables"));

  public static final EnvironmentVariableCoordinateFormatter INSTANCE =
      new EnvironmentVariableCoordinateFormatter();

  @Override
  public Optional<CommandPropertySyntax> formatParameterSyntax(LeafCommandProperty property,
      InvocationContext context) {
    EnvironmentVariableParameter annotation = property.getAnnotations().stream()
        .mapMulti(Streams.filterAndCast(EnvironmentVariableParameter.class)).findFirst()
        .orElse(null);
    if (annotation == null)
      return Optional.empty();

    return Optional.of(
        new CommandPropertySyntax(CATEGORY, annotation.variable(), List.of(annotation.variable())));
  }
}
