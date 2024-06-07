package com.sigpwned.discourse.core.format.help.coordinate;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntaxFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Streams;

public class PositionalParameterCoordinateFormatter implements CommandPropertySyntaxFormatter {
  public static final PositionalParameterCoordinateFormatter INSTANCE =
      new PositionalParameterCoordinateFormatter();

  @Override
  public Optional<List<String>> formatParameterSyntax(LeafCommandProperty property,
      InvocationContext context) {
    List<PositionalCoordinate> positionals = property.getCoordinates().stream()
        .mapMulti(Streams.filterAndCast(PositionalCoordinate.class)).toList();
    if (positionals.isEmpty())
      return Optional.empty();

    List<String> result = new ArrayList<>();
    for (PositionalCoordinate position : positionals) {
      // TODO Better value syntax from deserializer?
      // TODO refer to required for [name] vs <name>?
      String valueSyntax = "<" + property.getName() + ">";
      result.add(valueSyntax);
    }

    return Optional.of(unmodifiableList(result));
  }
}
