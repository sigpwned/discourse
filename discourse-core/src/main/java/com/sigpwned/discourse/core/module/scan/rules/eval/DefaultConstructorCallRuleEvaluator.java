package com.sigpwned.discourse.core.module.scan.rules.eval;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleEvaluator;
import com.sigpwned.discourse.core.util.Reflection;

public class DefaultConstructorCallRuleEvaluator implements RuleEvaluator {
  public static final DefaultConstructorCallRuleEvaluator INSTANCE =
      new DefaultConstructorCallRuleEvaluator();

  @Override
  public Optional<Optional<Object>> run(Map<String, Object> input, NamedRule rule) {
    if (rule.nominated() instanceof Constructor constructor
        && Modifier.isPublic(constructor.getModifiers())
        && Reflection.hasDefaultConstructorSignature(constructor)) {
      // Square deal. This is what we're here for.
    } else {
      // We don't execute this guy.
      return Optional.empty();
    }

    if (rule.antecedents().size() != 0) {
      // TODO better exception
      throw new IllegalArgumentException(
          "Default constructor rules must have exactly zero antecedents");
    }

    Object instance;
    try {
      instance = constructor.newInstance();
    } catch (IllegalArgumentException e) {
      // TODO better exception
      throw e;
    } catch (IllegalAccessException e) {
      // We check public, so this shouldn't happen
      throw new AssertionError(e);
    } catch (InstantiationException e) {
      // Welp, that's no good. The underlying code threw an exception.
      // TODO better exception
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }


    return Optional.of(Optional.of(instance));
  }

}
