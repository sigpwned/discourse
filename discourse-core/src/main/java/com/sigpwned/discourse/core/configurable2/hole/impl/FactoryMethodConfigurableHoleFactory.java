package com.sigpwned.discourse.core.configurable2.hole.impl;

import com.sigpwned.discourse.core.configurable2.Peg;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHole;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHoleCandidate;
import com.sigpwned.discourse.core.configurable2.hole.ConfigurableHoleFactory;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FactoryMethodConfigurableHoleFactory implements ConfigurableHoleFactory {

  @Override
  public Optional<ConfigurableHole> createHole(ConfigurableHoleCandidate candidate,
      List<Peg> pegs) {
    if (!(candidate.nominated() instanceof java.lang.reflect.Method method)) {
      return Optional.empty();
    }

    final int parameterCount = method.getParameterCount();
    if (parameterCount == 0) {
      return Optional.of(new ConfigurableHole(candidate.nominated(), Set.of(),
          Optional.of(new Peg("", candidate.nominated(), Optional.empty()))));
    }

    String[] parameterNames = new String[parameterCount];
    for (int i = 0; i < parameterCount; i++) {
      final Parameter parameter = method.getParameters()[i];
      parameterNames[i] = pegs.stream().filter(peg -> peg.nominated() == parameter).map(Peg::name)
          .findFirst().orElse(null);
      if (parameterNames[i] == null) {
        return Optional.empty();
      }
    }

    return Optional.of(new ConfigurableHole(candidate.nominated(), Set.of(parameterNames),
        Optional.of(new Peg("", candidate.nominated(), Optional.empty()))));
  }
}
