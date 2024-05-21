package com.sigpwned.discourse.core.invocation.phase.scan;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.util.Maybe;

public class NamingSchemeChain extends Chain<NamingScheme> implements NamingScheme {

  @Override
  public Maybe<String> name(Object object) {
    for (NamingScheme namingScheme : this) {
      Maybe<String> maybeName = namingScheme.name(object);
      if (maybeName.isDecided()) {
        return maybeName;
      }
    }
    return Maybe.maybe();
  }
}
