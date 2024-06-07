package com.sigpwned.discourse.core.format.help.coordinate;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.dialect.TokenFormatter;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntaxFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.util.Streams;

public class OptionParameterCoordinateFormatter implements CommandPropertySyntaxFormatter {
  public static final OptionParameterCoordinateFormatter INSTANCE =
      new OptionParameterCoordinateFormatter();

  @Override
  public Optional<List<String>> formatParameterSyntax(LeafCommandProperty property,
      InvocationContext context) {
    Dialect dialect = context.get(InvocationPipelineStep.DIALECT_KEY).orElseThrow();

    TokenFormatter tokenFormatter = dialect.newTokenFormatter();

    List<OptionCoordinate> options = property.getCoordinates().stream()
        .mapMulti(Streams.filterAndCast(OptionCoordinate.class)).toList();
    if (options.isEmpty())
      return Optional.empty();

    List<String> result = new ArrayList<>();
    for (OptionCoordinate option : options) {
      String optionSyntax =
          tokenFormatter.formatToken(new SwitchNameToken(option.getName(), false)).orElseThrow();
      // TODO Better value syntax from deserializer
      String valueSyntax = "<value>";
      result.add(optionSyntax + " " + valueSyntax);
    }

    return Optional.of(unmodifiableList(result));
  }
}
