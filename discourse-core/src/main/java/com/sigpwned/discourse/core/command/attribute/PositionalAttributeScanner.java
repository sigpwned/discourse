package com.sigpwned.discourse.core.command.attribute;

import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.command.model.AttributeMetadata;
import com.sigpwned.discourse.core.configurable.ConfigurableComponent;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.ParsePhase;
import com.sigpwned.discourse.core.util.Streams;
import java.util.Map;
import java.util.Optional;

public class PositionalAttributeScanner implements AttributeScanner {

  @Override
  public Optional<AttributeMetadata> scanForAttributes(ConfigurableComponent component) {
    PositionalParameter positional = component.getAnnotations().stream()
        .mapMulti(Streams.filterAndCast(PositionalParameter.class)).findFirst().orElse(null);
    if (positional == null) {
      return Optional.empty();
    }
    // TODO positional coordinate
    return Optional.of(
        new AttributeMetadata(Map.of(positional.position(), ParsePhase.POSITIONAL_TYPE)));
  }
}
