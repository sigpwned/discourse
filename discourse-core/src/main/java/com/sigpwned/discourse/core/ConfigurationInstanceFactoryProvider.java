package com.sigpwned.discourse.core;

import java.util.Optional;

public interface ConfigurationInstanceFactoryProvider {

  public <T> Optional<ConfigurationInstanceFactory<T>> getConfigurationInstanceFactory(
      Class<T> type);
}
