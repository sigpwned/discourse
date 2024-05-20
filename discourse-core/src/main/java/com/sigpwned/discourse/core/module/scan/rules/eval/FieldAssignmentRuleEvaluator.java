package com.sigpwned.discourse.core.module.scan.rules.eval;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleEvaluator;
import com.sigpwned.discourse.core.util.Reflection;

public class FieldAssignmentRuleEvaluator implements RuleEvaluator {

  @Override
  public Optional<Object> run(Map<String, Object> input, NamedRule rule) {
    if (rule.nominated() instanceof Field field && Modifier.isPublic(field.getModifiers())
        && Reflection.isMutableInstanceField(field)) {
      // Square deal. This is what we're here for.
    } else {
      // We don't execute this guy.
      return Optional.empty();
    }

    if (rule.antecedents().size() != 2) {
      // TODO better exception
      throw new IllegalArgumentException(
          "Field assignment rules must have exactly two antecedents");
    }

    if (!rule.antecedents().contains("")) {
      // TODO better exception
      throw new IllegalArgumentException(
          "First antecedent of a field assignment rule must be 'instance'");
    }

    String valueName =
        rule.antecedents().stream().filter(s -> !s.equals("")).findFirst().orElseThrow();

    Object instance = input.get("");
    Object value = input.get(valueName);

    try {
      field.set(instance, value);
    } catch (IllegalArgumentException e) {
      // TODO better exception
      throw e;
    } catch (IllegalAccessException e) {
      // We check public, so this shouldn't happen
      throw new AssertionError(e);
    }

    return Optional.empty();
  }

}
