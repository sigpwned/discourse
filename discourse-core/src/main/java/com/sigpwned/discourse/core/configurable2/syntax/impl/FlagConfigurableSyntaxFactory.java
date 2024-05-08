package com.sigpwned.discourse.core.configurable2.syntax.impl;

import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntax;
import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntaxCandidate;
import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntaxFactory;
import com.sigpwned.discourse.core.phase.parse.ParsePhase;
import com.sigpwned.discourse.core.util.Streams;
import java.util.Map;
import java.util.Optional;

public class FlagConfigurableSyntaxFactory implements ConfigurableSyntaxFactory {

  @Override
  public Optional<ConfigurableSyntax> createSyntax(ConfigurableSyntaxCandidate candidate) {
    FlagParameter flag = candidate.annotations().stream()
        .mapMulti(Streams.filterAndCast(FlagParameter.class)).findFirst().orElse(null);
    if (flag == null) {
      return Optional.empty();
    }

    Map<Object, String> coordinates;
    if (flag.shortName() != null && flag.longName() != null) {
      coordinates = Map.of(flag.shortName(), ParsePhase.FLAG_TYPE, flag.longName(),
          ParsePhase.FLAG_TYPE);
    } else if (flag.shortName() != null) {
      coordinates = Map.of(flag.shortName(), ParsePhase.FLAG_TYPE);
    } else if (flag.longName() != null) {
      coordinates = Map.of(flag.longName(), ParsePhase.FLAG_TYPE);
    } else {
      // TODO better exception
      throw new IllegalArgumentException(
          "FlagParameter must define at least one of shortName, longName");
    }

    return Optional.of(new ConfigurableSyntax(candidate.nominated(), candidate.genericType(),
        candidate.annotations(), coordinates));
  }
}
