package com.sigpwned.discourse.core.invocation.phase.scan.impl.rules;

import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.DetectedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.syntax.NamedSyntax;
import java.util.List;
import java.util.Optional;

public interface RuleDetector {

  public Optional<DetectedRule> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate);
}
