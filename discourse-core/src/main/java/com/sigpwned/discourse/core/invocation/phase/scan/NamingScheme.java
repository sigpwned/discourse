package com.sigpwned.discourse.core.invocation.phase.scan;

import java.util.Optional;

public interface NamingScheme {

  public Optional<String> name(Object object);
}
