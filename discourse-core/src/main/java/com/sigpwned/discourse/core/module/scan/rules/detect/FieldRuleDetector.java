package com.sigpwned.discourse.core.module.scan.rules.detect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.model.RuleDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.rules.RuleDetector;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Reflection;
import com.sigpwned.discourse.core.util.collectors.Only;

public class FieldRuleDetector implements RuleDetector {
  public static final FieldRuleDetector INSTANCE = new FieldRuleDetector();

  @Override
  public Maybe<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate, InvocationContext context) {
    if (candidate.nominated() instanceof Field nominated
        && Modifier.isPublic(nominated.getModifiers())
        && Reflection.isMutableInstanceField(nominated)) {
      // Groovy. That's what we're here for.
    } else {
      // Welp, we're done here.
      return Maybe.maybe();
    }

    NamedSyntax candidateSyntax = syntax.stream().filter(si -> {
      System.out.println(si.nominated());
      System.out.println(nominated);
      System.out.println(si.nominated().equals(nominated));
      System.out.println(si.nominated() == nominated);
      return si.nominated() == nominated;
    }).collect(Only.toOnly()).orElse(null,
        () -> new IllegalArgumentException("too many syntax for field " + nominated.getName()));
    if (candidateSyntax == null) {
      // This is fine. Not every field is going to be used.
      return Maybe.maybe();
    }

    // TODO how should we handle the instance parameter?
    Set<String> antecedents = Set.of("", candidateSyntax.name());

    // A field assignment produces no new values
    boolean hasConsequent = false;

    return Maybe.yes(new RuleDetection(antecedents, hasConsequent));
  }
}
