package com.sigpwned.discourse.core.chain;

import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactoryScanner;
import com.sigpwned.discourse.core.util.Chains;
import java.util.Optional;

public class ConfigurableInstanceFactoryScannerChain extends
    Chain<ConfigurableInstanceFactoryScanner> {

  public <T> Optional<ConfigurableInstanceFactory<T>> scanForInstanceFactory(
      Class<T> type) {
    return Chains.stream(this)
        .flatMap(provider -> provider.scanForInstanceFactory(type).stream()).findFirst();
  }
}
