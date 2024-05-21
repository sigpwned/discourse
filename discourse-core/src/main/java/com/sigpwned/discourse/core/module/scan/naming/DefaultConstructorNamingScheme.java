package com.sigpwned.discourse.core.module.scan.naming;

import java.lang.reflect.Constructor;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Reflection;

public class DefaultConstructorNamingScheme implements NamingScheme {
  public static final DefaultConstructorNamingScheme INSTANCE =
      new DefaultConstructorNamingScheme();

  @Override
  public Maybe<String> name(Object object) {
    if (!(object instanceof Constructor constructor))
      return Maybe.maybe();
    if (!Reflection.hasDefaultConstructorSignature(constructor))
      return Maybe.maybe();

    // TODO how do we handle mixins?
    // TODO default constructor label
    return Maybe.yes("");
  }
}
