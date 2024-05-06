package com.sigpwned.discourse.core.configuration;

import java.lang.reflect.Type;
import java.util.Optional;

public interface ValueFactoryChain {

  public Optional<ValueFactory> findValueFactory(Type genericType);
}
