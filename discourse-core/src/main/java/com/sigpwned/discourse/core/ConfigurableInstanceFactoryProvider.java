package com.sigpwned.discourse.core;

import java.util.Optional;

public interface ConfigurableInstanceFactoryProvider {

  public <T> Optional<ConfigurableInstanceFactory<T>> getConfigurationInstanceFactory(
      Class<T> type);
}
