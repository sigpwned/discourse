package com.sigpwned.discourse.core.invocation.phase.scan;

import com.sigpwned.discourse.core.util.Maybe;

public interface NamingScheme {

  public Maybe<String> name(Object object);
}
