package com.sigpwned.discourse.core.configurable3.rule;

import com.sigpwned.discourse.core.configurable3.syntax.NamedSyntax;
import java.util.List;
import java.util.Optional;

public interface RuleDetector {

  public Optional<DetectedRule> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate);
}
