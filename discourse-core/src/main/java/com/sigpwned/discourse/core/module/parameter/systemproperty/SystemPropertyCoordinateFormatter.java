package com.sigpwned.discourse.core.module.parameter.systemproperty;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.format.help.CommandPropertyCategory;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntax;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntaxFormatter;
import com.sigpwned.discourse.core.format.help.HelpMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Streams;

public class SystemPropertyCoordinateFormatter implements CommandPropertySyntaxFormatter {
  public static final int CATEGORY_PRIORITY = 20000;

  public static final CommandPropertyCategory CATEGORY =
      new CommandPropertyCategory(CATEGORY_PRIORITY, HelpMessage.of("System Properties"));

  public static final SystemPropertyCoordinateFormatter INSTANCE =
      new SystemPropertyCoordinateFormatter();

  @Override
  public Optional<CommandPropertySyntax> formatParameterSyntax(LeafCommandProperty property,
      InvocationContext context) {
    SystemPropertyParameter annotation = property.getAnnotations().stream()
        .mapMulti(Streams.filterAndCast(SystemPropertyParameter.class)).findFirst().orElse(null);
    if (annotation == null)
      return Optional.empty();

    return Optional.of(
        new CommandPropertySyntax(CATEGORY, annotation.property(), List.of(annotation.property())));
  }
}
