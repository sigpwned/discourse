package com.sigpwned.discourse.core.invocation.phase.scan.rules;

import java.util.List;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.model.RuleDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import com.sigpwned.discourse.core.util.Maybe;

public interface RuleDetector {

  public Maybe<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate, InvocationContext context);
}
