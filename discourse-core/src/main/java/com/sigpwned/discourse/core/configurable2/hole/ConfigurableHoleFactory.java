package com.sigpwned.discourse.core.configurable2.hole;

import com.sigpwned.discourse.core.configurable2.Peg;
import java.util.List;
import java.util.Optional;

public interface ConfigurableHoleFactory {

  public Optional<ConfigurableHole> createHole(ConfigurableHoleCandidate candidate, List<Peg> pegs);
}
