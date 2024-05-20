package com.sigpwned.discourse.core.module.scan.rules.nominate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleNominator;
import com.sigpwned.discourse.core.util.Reflection;

public class FieldRuleNominator implements RuleNominator {
  @Override
  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax) {
    List<CandidateRule> result = new ArrayList<>();

    for (Class<?> ancestor = clazz; ancestor != null; ancestor = ancestor.getSuperclass()) {
      for (Field field : ancestor.getDeclaredFields()) {
        if (Modifier.isPublic(field.getModifiers()) && Reflection.isMutableInstanceField(field)) {
          result.add(
              new CandidateRule(field, field.getGenericType(), List.of(field.getAnnotations())));
        }
      }
    }

    return result;
  }
}
