package com.sigpwned.discourse.core.module.scan.rules.detect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.invocation.model.RuleDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetector;
import com.sigpwned.discourse.core.util.Reflection;
import com.sigpwned.discourse.core.util.collectors.Only;

public class FieldRuleDetector implements RuleDetector {

  @Override
  public Optional<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate) {
    if (candidate.nominated() instanceof Field nominated
        && Modifier.isPublic(nominated.getModifiers())
        && Reflection.isMutableInstanceField(nominated)) {
      // Groovy. That's what we're here for.
    } else {
      // Welp, we're done here.
      return Optional.empty();
    }

    NamingScheme naming = null;

    String name = naming.name(nominated).orElse(null);
    if (name == null) {
      // This is fine. Not every field is going to be used.
      return Optional.empty();
    }

    NamedSyntax source = syntax.stream().filter(si -> si.name().equals(name)).collect(Only.toOnly())
        .orElse(null, () -> {
          // TODO better exception?
          return new IllegalArgumentException("too many syntax for field " + name);
        });
    if (source == null) {
      // This is fine. Not every field is going to be used.
      return Optional.empty();
    }

    // A field depends on the containing instance and the value to set.
    Set<String> antecedents = Set.of("", name);

    // A field assignment produces no new values
    boolean hasConsequent = false;

    return Optional.of(new RuleDetection(antecedents, hasConsequent));
  }
}
