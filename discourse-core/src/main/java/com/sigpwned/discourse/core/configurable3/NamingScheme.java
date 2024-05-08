package com.sigpwned.discourse.core.configurable3;

import java.util.Optional;

public interface NamingScheme {

  public Optional<String> name(Object nominated);
}
