package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import java.util.List;

public record PreparedArgument(String name, List<DeserializedArgument> deserializedArguments,
    Object sinkedArgumentValue) {

  public PreparedArgument {
    name = requireNonNull(name);
    deserializedArguments = unmodifiableList(deserializedArguments);
  }
}
