package com.sigpwned.discourse.core.module.core.scan.naming;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import com.sigpwned.discourse.core.annotation.DiscourseCreator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.NamingScheme;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Reflection;

public class CreatorNamingScheme implements NamingScheme {
  public static final CreatorNamingScheme INSTANCE = new CreatorNamingScheme();

  @Override
  public Maybe<String> name(Object object) {
    if (object instanceof Constructor<?> constructor
        && constructor.getAnnotation(DiscourseCreator.class) != null
        && Modifier.isPublic(constructor.getModifiers())
        && !Reflection.hasDefaultConstructorSignature(constructor)) {
      // We only nominate constructors from leaf command classes, so this has to be the instance.
      // TODO instance constant name?
      return Maybe.yes("");
    }

    if (object instanceof Method method && method.getAnnotation(DiscourseCreator.class) != null
        && Modifier.isPublic(method.getModifiers())
        && Reflection.hasFactoryMethodSignature(method)) {
      // The only way to name this is with @DiscourseAttribute, which is handled elsewhere.
    }

    return Maybe.maybe();
  }
}
