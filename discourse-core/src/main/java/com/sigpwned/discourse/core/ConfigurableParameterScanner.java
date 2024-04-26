package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.util.List;

public interface ConfigurableParameterScanner {

  public List<ConfigurationParameter> scanForParameters(Class<?> type, InvocationContext context);
}
