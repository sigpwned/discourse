package com.sigpwned.discourse.core.dialect.windows.format;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.dialect.ArgFormatter;
import com.sigpwned.discourse.core.dialect.windows.WindowsDialectElement;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.l11n.util.UserMessages;

public class OptionWindowsArgFormatter implements ArgFormatter, WindowsDialectElement {
  @Override
  public Optional<List<List<String>>> formatArg(PlannedCommandProperty property,
      Coordinate coordinate) {
    if (!(coordinate instanceof OptionCoordinate optionCoordinate))
      return Optional.empty();

    SwitchName name = optionCoordinate.getName();

    UserMessage type = property.getDeserializer().name().orElse(DEFAULT_TYPE_MESSAGE);

    return Optional
        .of(List.of(List.of(SWITCH_NAME_PREFIX + name, "<" + UserMessages.render(type) + ">")));
  }
}
