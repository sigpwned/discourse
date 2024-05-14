package com.sigpwned.discourse.core.invocation.phase.scan.impl;

import java.util.Optional;

public interface NamingScheme {

  public Optional<String> name(Object object);
}
