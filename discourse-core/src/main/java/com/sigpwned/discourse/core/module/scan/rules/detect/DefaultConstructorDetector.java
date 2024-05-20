package com.sigpwned.discourse.core.module.scan.rules.detect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.invocation.model.RuleDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetector;
import com.sigpwned.discourse.core.util.Reflection;

public class DefaultConstructorDetector implements RuleDetector {

  @Override
  public Optional<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate) {
    if (candidate.nominated() instanceof Constructor<?> nominated
        && Modifier.isPublic(nominated.getModifiers())
        && Reflection.hasDefaultConstructorSignature(nominated)) {
      // Groovy. That's what we're here for.
    } else {
      // Welp, we're done here.
      return Optional.empty();
    }

    // A default constructor has no antecedents, by definition
    Set<String> antecedents = Set.of();

    // A default constructor produces a new value, by definition
    boolean hasConsequent = true;

    return Optional.of(new RuleDetection(antecedents, hasConsequent));
  }
}
