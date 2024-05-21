package com.sigpwned.discourse.core.module.scan.rules.nominate;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominator;
import com.sigpwned.discourse.core.util.Reflection;

public class SetterMethodRuleNominator implements RuleNominator {
  public static final SetterMethodRuleNominator INSTANCE = new SetterMethodRuleNominator();

  @Override
  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax,
      InvocationContext context) {
    List<CandidateRule> result = new ArrayList<>();

    for (Class<?> ancestor = clazz; ancestor != null; ancestor = ancestor.getSuperclass()) {
      for (Method method : ancestor.getDeclaredMethods()) {
        if (Modifier.isPublic(method.getModifiers())
            && Reflection.hasInstanceSetterSignature(method)) {
          result.add(new CandidateRule(method, method.getGenericParameterTypes()[0],
              List.of(method.getAnnotations())));
        }
      }
    }

    return result;
  }
}
