package com.sigpwned.discourse.core.module.parameter.environmentvariable;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.command.planned.PlannedCommandProperty;
import com.sigpwned.discourse.core.dialect.ArgFormatter;
import com.sigpwned.discourse.core.l11n.UserMessage;
import com.sigpwned.discourse.core.l11n.util.UserMessages;
import com.sigpwned.discourse.core.util.Localization;

public class EnvironmentVariableArgFormatter implements ArgFormatter {
  public static final UserMessage DEFAULT_TYPE_MESSAGE = Localization.DEFAULT_TYPE_MESSAGE;

  @Override
  public Optional<List<List<String>>> formatArg(PlannedCommandProperty property,
      Coordinate coordinate) {
    if (!(coordinate instanceof EnvironmentVariableCoordinate variableCoordinate))
      return Optional.empty();

    UserMessage type = property.getDeserializer().name().orElse(DEFAULT_TYPE_MESSAGE);

    return Optional
        .of(List.of(List.of(variableCoordinate.getVariableName(), "=", UserMessages.render(type))));
  }
}
