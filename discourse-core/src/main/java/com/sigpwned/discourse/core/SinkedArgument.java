package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import java.util.List;

public record SinkedArgument(String name, List<DeserializedArgument> deserializedArguments,
    Object sinkedArgumentValue) {

  public SinkedArgument {
    name = requireNonNull(name);
    deserializedArguments = unmodifiableList(deserializedArguments);
  }
}
