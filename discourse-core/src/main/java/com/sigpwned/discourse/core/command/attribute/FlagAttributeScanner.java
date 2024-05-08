package com.sigpwned.discourse.core.command.attribute;

import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.command.model.AttributeMetadata;
import com.sigpwned.discourse.core.configurable.ConfigurableComponent;
import com.sigpwned.discourse.core.phase.parse.ParsePhase;
import com.sigpwned.discourse.core.util.Streams;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FlagAttributeScanner implements AttributeScanner {

  @Override
  public Optional<AttributeMetadata> scanForAttributes(ConfigurableComponent component) {
    FlagParameter flag = component.getAnnotations().stream()
        .mapMulti(Streams.filterAndCast(FlagParameter.class)).findFirst().orElse(null);
    if (flag == null) {
      return Optional.empty();
    }

    Map<Object, String> coordinates = new HashMap<>(2);
    if (flag.shortName() != null) {
      // TODO short name
      coordinates.put("-" + flag.shortName(), ParsePhase.FLAG_TYPE);
    }
    if (flag.longName() != null) {
      // TODO long name
      coordinates.put("--" + flag.longName(), ParsePhase.FLAG_TYPE);
    }

    return Optional.of(new AttributeMetadata(coordinates));
  }
}
