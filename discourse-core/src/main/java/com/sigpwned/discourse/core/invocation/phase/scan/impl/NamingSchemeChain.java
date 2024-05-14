package com.sigpwned.discourse.core.invocation.phase.scan.impl;

import com.sigpwned.discourse.core.Chain;
import java.util.Optional;

public class NamingSchemeChain extends Chain<NamingScheme> implements NamingScheme {

  @Override
  public Optional<String> name(Object object) {
    for (NamingScheme namingScheme : this) {
      Optional<String> name = namingScheme.name(object);
      if (name.isPresent()) {
        return name;
      }
    }
    return Optional.empty();
  }
}
