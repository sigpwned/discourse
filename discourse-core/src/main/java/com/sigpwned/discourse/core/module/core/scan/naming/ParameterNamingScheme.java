package com.sigpwned.discourse.core.module.core.scan.naming;

import java.lang.reflect.Parameter;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.NamingScheme;
import com.sigpwned.discourse.core.util.Maybe;

public class ParameterNamingScheme implements NamingScheme {
  public static final ParameterNamingScheme INSTANCE = new ParameterNamingScheme();

  @Override
  public Maybe<String> name(Object object) {
    if (!(object instanceof Parameter parameter)) {
      return Maybe.maybe();
    }
    if (!parameter.isNamePresent()) {
      return Maybe.maybe();
    }
    return Maybe.yes(parameter.getName());
  }
}
