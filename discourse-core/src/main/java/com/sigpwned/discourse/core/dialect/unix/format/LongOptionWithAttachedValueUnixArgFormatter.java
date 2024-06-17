package com.sigpwned.discourse.core.dialect.unix.format;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.dialect.ArgFormatter;
import com.sigpwned.discourse.core.dialect.unix.UnixDialectElement;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.l11n.util.UserMessages;

public class LongOptionWithAttachedValueUnixArgFormatter
    implements ArgFormatter, UnixDialectElement {
  @Override
  public Optional<List<List<String>>> formatArg(PlannedCommandProperty property,
      Coordinate coordinate) {
    if (!(coordinate instanceof OptionCoordinate option))
      return Optional.empty();

    if (option.getName().length() == 1)
      return Optional.empty();

    UserMessage type = property.getDeserializer().name().orElse(DEFAULT_TYPE_MESSAGE);

    return Optional.of(List
        .of(List.of(LONG_NAME_PREFIX + option.getName() + "=<" + UserMessages.render(type) + ">")));
  }
}
