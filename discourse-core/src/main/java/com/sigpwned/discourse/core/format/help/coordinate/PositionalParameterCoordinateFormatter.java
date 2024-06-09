package com.sigpwned.discourse.core.format.help.coordinate;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;
import com.sigpwned.discourse.core.command.LeafCommandProperty;
import com.sigpwned.discourse.core.format.help.CommandPropertyCategory;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntax;
import com.sigpwned.discourse.core.format.help.CommandPropertySyntaxFormatter;
import com.sigpwned.discourse.core.format.help.HelpMessage;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Streams;

public class PositionalParameterCoordinateFormatter implements CommandPropertySyntaxFormatter {
  public static final int CATEGORY_PRIORITY = 2000;

  public static final CommandPropertyCategory CATEGORY =
      new CommandPropertyCategory(CATEGORY_PRIORITY, HelpMessage.of("Positional Arguments"));

  public static final PositionalParameterCoordinateFormatter INSTANCE =
      new PositionalParameterCoordinateFormatter();

  @Override
  public Optional<CommandPropertySyntax> formatParameterSyntax(LeafCommandProperty property,
      InvocationContext context) {
    List<PositionalCoordinate> positionals = property.getCoordinates().stream()
        .mapMulti(Streams.filterAndCast(PositionalCoordinate.class)).toList();
    if (positionals.isEmpty())
      return Optional.empty();

    List<String> result = new ArrayList<>();
    for (@SuppressWarnings("unused")
    PositionalCoordinate position : positionals) {
      // TODO Better value syntax from deserializer?
      // TODO refer to required for [name] vs <name>?
      String valueSyntax = property.getName();
      result.add(valueSyntax);
    }

    Integer key = positionals.stream().map(PositionalCoordinate::getPosition)
        .min(Comparator.naturalOrder()).orElseThrow();

    return Optional.of(new CommandPropertySyntax(CATEGORY, key, unmodifiableList(result)));
  }
}
