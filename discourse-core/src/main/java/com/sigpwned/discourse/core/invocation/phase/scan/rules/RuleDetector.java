package com.sigpwned.discourse.core.invocation.phase.scan.rules;

import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.invocation.model.RuleDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;

public interface RuleDetector {

  public Optional<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate);
}
