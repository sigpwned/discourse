package com.sigpwned.discourse.core.configurable2;

import com.sigpwned.discourse.core.configurable2.syntax.ConfigurableSyntax;
import java.util.Optional;

public interface ConfigurableAttributeNamingScheme {

  public Optional<String> nameAttribute(ConfigurableSyntax syntax);
}
