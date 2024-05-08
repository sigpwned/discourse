package com.sigpwned.discourse.core.command.attribute;

import com.sigpwned.discourse.core.command.model.AttributeMetadata;
import com.sigpwned.discourse.core.configurable.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.element.ConfigurableElement;
import java.util.Optional;

public interface AttributeScanner {

  public Optional<AttributeMetadata> scanForAttributes(String name, ConfigurableComponent component,
      ConfigurableElement element);
}
