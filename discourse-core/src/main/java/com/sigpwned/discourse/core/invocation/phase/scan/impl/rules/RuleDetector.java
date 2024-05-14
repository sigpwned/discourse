package com.sigpwned.discourse.core.invocation.phase.scan.impl.rules;

import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.DetectedRule;
import java.util.List;
import java.util.Optional;

public interface RuleDetector {

  public Optional<DetectedRule> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate);
}
