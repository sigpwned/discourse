package com.sigpwned.discourse.core.format.help.coordinate;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.dialect.TokenFormatter;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntax;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntaxFormatter;
import com.sigpwned.discourse.core.module.parameter.flag.FlagCoordinate;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.util.Streams;

public class FlagParameterCoordinateFormatter implements CommandPropertySyntaxFormatter {
  public static final FlagParameterCoordinateFormatter INSTANCE =
      new FlagParameterCoordinateFormatter();

  @Override
  public Optional<CommandPropertySyntax> formatParameterSyntax(LeafCommandProperty property,
      InvocationContext context) {
    Dialect dialect = context.get(InvocationPipelineStep.DIALECT_KEY).orElseThrow();

    TokenFormatter tokenFormatter = dialect.newTokenFormatter();

    List<FlagCoordinate> flags = property.getCoordinates().stream()
        .mapMulti(Streams.filterAndCast(FlagCoordinate.class)).toList();
    if (flags.isEmpty())
      return Optional.empty();

    List<String> result = new ArrayList<>();
    for (FlagCoordinate flag : flags) {
      String optionSyntax =
          tokenFormatter.formatToken(new SwitchNameToken(flag.getName(), false)).orElseThrow();
      result.add(optionSyntax);
    }

    SwitchName key =
        flags.stream().map(FlagCoordinate::getName).min(Comparator.naturalOrder()).orElseThrow();

    return Optional.of(new CommandPropertySyntax(OptionParameterCoordinateFormatter.CATEGORY, key,
        unmodifiableList(result)));
  }
}
