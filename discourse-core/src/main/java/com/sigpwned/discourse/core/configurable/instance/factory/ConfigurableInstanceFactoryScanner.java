package com.sigpwned.discourse.core.configurable.instance.factory;

import java.util.Optional;

public interface ConfigurableInstanceFactoryScanner {

  public <T> Optional<ConfigurableInstanceFactory<T>> scanForInstanceFactory(
      Class<T> type);
}
