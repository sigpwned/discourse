package com.sigpwned.discourse.core.invocation.phase.scan.rules;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.DetectedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import java.util.List;
import java.util.Optional;

public class RuleDetectorChain extends Chain<RuleDetector> implements RuleDetector {

  @Override
  public Optional<DetectedRule> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate) {
    for (RuleDetector detector : this) {
      Optional<DetectedRule> rule = detector.detectRule(clazz, syntax, candidate);
      if (rule.isPresent()) {
        return rule;
      }
    }
    return Optional.empty();
  }
}
