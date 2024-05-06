package com.sigpwned.discourse.core.command.attribute;

import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.command.model.AttributeMetadata;
import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.phase.parse.ParsePhase;
import com.sigpwned.discourse.core.util.Streams;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OptionAttributeScanner implements AttributeScanner {

  @Override
  public Optional<AttributeMetadata> scanForAttributes(ConfigurableComponent component) {
    OptionParameter option = component.getAnnotations().stream()
        .mapMulti(Streams.filterAndCast(OptionParameter.class)).findFirst().orElse(null);
    if (option == null) {
      return Optional.empty();
    }

    Map<Object, String> coordinates = new HashMap<>(2);
    if (option.shortName() != null) {
      // TODO short name
      coordinates.put("-" + option.shortName(), ParsePhase.OPTION_TYPE);
    }
    if (option.longName() != null) {
      // TODO long name
      coordinates.put("--" + option.longName(), ParsePhase.OPTION_TYPE);
    }

    return Optional.of(new AttributeMetadata(coordinates));
  }
}
