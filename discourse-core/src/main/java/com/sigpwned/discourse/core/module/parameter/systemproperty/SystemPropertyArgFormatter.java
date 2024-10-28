package com.sigpwned.discourse.core.module.parameter.systemproperty;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.dialect.ArgFormatter;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.l11n.util.UserMessages;
import com.sigpwned.discourse.core.util.Localization;

public class SystemPropertyArgFormatter implements ArgFormatter {
  public static final UserMessage DEFAULT_TYPE_MESSAGE = Localization.DEFAULT_TYPE_MESSAGE;

  @Override
  public Optional<List<List<String>>> formatArg(PlannedCommandProperty property,
      Coordinate coordinate) {
    if (!(coordinate instanceof SystemPropertyCoordinate propertyCoordinate))
      return Optional.empty();

    UserMessage type = property.getDeserializer().name().orElse(DEFAULT_TYPE_MESSAGE);

    // TODO Add -D prefix?
    return Optional
        .of(List.of(List.of(propertyCoordinate.getPropertyName(), "=", UserMessages.render(type))));
  }
}
