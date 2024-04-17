package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.coordinate.Coordinate;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.util.Optional;

@FunctionalInterface
public interface ConfigurationParameterResolver {

  public Optional<ConfigurationParameter> resolveConfigurationParameter(Coordinate coordinate);
}
