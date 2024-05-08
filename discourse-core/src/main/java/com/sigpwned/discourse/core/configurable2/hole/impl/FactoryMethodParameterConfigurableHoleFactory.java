package com.sigpwned.discourse.core.configurable2.hole.impl;

import com.sigpwned.discourse.core.configurable2.Peg;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHole;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHoleCandidate;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHoleFactory;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FactoryMethodParameterConfigurableHoleFactory implements ConfigurableHoleFactory {

  @Override
  public Optional<ConfigurableHole> createHole(ConfigurableHoleCandidate candidate,
      List<Peg> pegs) {
    if (!(candidate.nominated() instanceof java.lang.reflect.Parameter parameter)) {
      return Optional.empty();
    }

    String name = pegs.stream().filter(peg -> peg.nominated() == parameter).map(Peg::name)
        .findFirst().orElse(null);
    if (name == null) {
      return Optional.empty();
    }

    return Optional.of(new ConfigurableHole(candidate.nominated(), Set.of(name),
        Optional.of(new Peg(name, candidate.nominated(), Optional.empty()))));
  }
}
