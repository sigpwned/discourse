package com.sigpwned.discourse.core.command.planned;

import static java.util.Objects.requireNonNull;
import java.util.Optional;

public class ParentCommand {
  private final String discriminator;
  private final String description;

  public ParentCommand(String discriminator, String description) {
    this.discriminator = requireNonNull(discriminator);
    this.description = description;
  }

  public String getDiscriminator() {
    return discriminator;
  }

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }
}
