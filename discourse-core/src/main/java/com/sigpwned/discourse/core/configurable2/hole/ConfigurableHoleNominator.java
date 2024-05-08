package com.sigpwned.discourse.core.configurable2.hole;

import java.util.List;

public interface ConfigurableHoleNominator {

  public List<ConfigurableHoleCandidate> nominateHoles(Class<?> type);
}
