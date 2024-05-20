package com.sigpwned.discourse.core.module.scan.rules.nominate;

import java.lang.reflect.Constructor;
import java.util.List;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominator;

public class DefaultConstructorNominator implements RuleNominator {
  @Override
  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax) {
    Constructor<?> constructor;
    try {
      constructor = clazz.getConstructor();
    } catch (NoSuchMethodException e) {
      // That's fine. Not all classes have a default constructor.
      return List.of();
    }

    return List.of(new CandidateRule(constructor, clazz, List.of(constructor.getAnnotations())));
  }
}
