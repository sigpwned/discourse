package com.sigpwned.discourse.core.command.model;

import static java.util.Collections.*;

import java.util.Map;

public record AttributeMetadata(Map<Object, String> coordinates) {

  public AttributeMetadata {
    coordinates = unmodifiableMap(coordinates);
    if (coordinates.isEmpty()) {
      // This is unusual, but it's not an error. This just means that this attribute is not settable
      // directly using coordinates from the command line. Instead, it is derived from other
      // attributes that are settable from the command line.
    }
  }
}
