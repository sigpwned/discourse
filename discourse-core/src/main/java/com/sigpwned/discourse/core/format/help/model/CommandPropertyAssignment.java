package com.sigpwned.discourse.core.format.help.model;

import static java.util.Objects.requireNonNull;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.l11n.UserMessage;

public class CommandPropertyAssignment {
  private final Coordinate coordinate;
  private final UserMessage value;

  public CommandPropertyAssignment(Coordinate coordinate, UserMessage value) {
    this.coordinate = requireNonNull(coordinate);
    this.value = value;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public Optional<UserMessage> getValue() {
    return Optional.ofNullable(value);
  }
}

