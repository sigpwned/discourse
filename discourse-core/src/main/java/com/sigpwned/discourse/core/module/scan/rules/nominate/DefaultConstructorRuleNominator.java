package com.sigpwned.discourse.core.module.scan.rules.nominate;

import static java.util.Collections.emptyList;
import java.lang.reflect.Constructor;
import java.util.List;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominator;
import com.sigpwned.discourse.core.util.Reflection;

public class DefaultConstructorRuleNominator implements RuleNominator {
  public static final DefaultConstructorRuleNominator INSTANCE =
      new DefaultConstructorRuleNominator();

  @Override
  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax,
      InvocationContext context) {
    Constructor<?> constructor = null;
    for (Constructor<?> candidate : clazz.getConstructors()) {
      if (Reflection.hasDefaultConstructorSignature(candidate)) {
        constructor = candidate;
        break;
      }
    }
    if (constructor == null)
      return emptyList();
    return List.of(new CandidateRule(constructor, clazz, List.of(constructor.getAnnotations())));
  }
}
