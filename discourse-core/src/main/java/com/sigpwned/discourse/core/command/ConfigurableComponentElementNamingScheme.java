package com.sigpwned.discourse.core.command;

import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.element.ConfigurableElement;
import java.util.Optional;

public interface ConfigurableComponentElementNamingScheme {

  public Optional<String> nameConfigurableElement(ConfigurableComponent component,
      ConfigurableElement element);
}
