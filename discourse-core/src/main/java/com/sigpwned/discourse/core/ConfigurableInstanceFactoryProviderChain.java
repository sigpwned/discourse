package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.util.Chains;
import java.util.Optional;

public class ConfigurableInstanceFactoryProviderChain extends
    Chain<ConfigurableInstanceFactoryProvider> {

  public <T> Optional<ConfigurableInstanceFactory<T>> getConfigurationInstanceFactory(
      Class<T> type) {
    return Chains.stream(this)
        .flatMap(provider -> provider.getConfigurationInstanceFactory(type).stream()).findFirst();
  }
}
