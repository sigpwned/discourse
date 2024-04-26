package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import java.util.List;

public interface ConfigurableComponentScanner {

  public List<ConfigurableComponent> scanForComponents(Class<?> rawType, InvocationContext context);
}
