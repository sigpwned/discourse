package com.sigpwned.discourse.core.configurable;

import java.util.Optional;

public interface ConfigurableComponentFactory {

  public Optional<ConfigurableComponent> createConfigurableComponent(
      CandidateConfigurableComponent candidateComponent);
}
