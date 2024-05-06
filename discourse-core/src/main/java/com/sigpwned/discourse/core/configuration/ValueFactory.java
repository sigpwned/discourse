package com.sigpwned.discourse.core.configuration;

import com.sigpwned.discourse.core.configuration.model.ConfigurationArguments;
import java.lang.reflect.Type;

public interface ValueFactory {

  public boolean handlesType(Type genericType);

  public Object createValue(ConfigurationArguments args, ValueFactoryChain chain);
}
