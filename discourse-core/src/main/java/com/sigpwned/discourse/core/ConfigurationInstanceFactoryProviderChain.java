package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.util.Chains;
import java.util.Optional;

public class ConfigurationInstanceFactoryProviderChain extends
    Chain<ConfigurationInstanceFactoryProvider> {

  public <T> Optional<ConfigurationInstanceFactory<T>> getConfigurationInstanceFactory(
      Class<T> type) {
    return Chains.stream(this)
        .flatMap(provider -> provider.getConfigurationInstanceFactory(type).stream()).findFirst();
  }
}
