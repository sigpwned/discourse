package com.sigpwned.discourse.core.module.scan.rules.detect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.invocation.model.RuleDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetector;
import com.sigpwned.discourse.core.util.Reflection;

public class SetterRuleDetector implements RuleDetector {

  @Override
  public Optional<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate) {
    if (candidate.nominated() instanceof Method nominated
        && Modifier.isPublic(nominated.getModifiers())
        && Reflection.hasInstanceSetterSignature(nominated)) {
      // Groovy. That's what we're here for.
    } else {
      // Welp, we're done here.
      return Optional.empty();
    }

    // We need to find the name of the value to pull from the syntax.
    String name = null;
    for (NamedSyntax si : syntax) {
      if (si.nominated() == nominated) {
        name = si.name();
        break;
      }
    }
    if (name == null) {
      // That's fine. Not every field is going to be used.
      return Optional.empty();
    }

    // A setter depends on the containing instance and the value to set.
    Set<String> antecedents = Set.of("", name);

    // A setter produces no new values
    boolean hasConsequent = false;

    return Optional.of(new RuleDetection(antecedents, hasConsequent));
  }
}
