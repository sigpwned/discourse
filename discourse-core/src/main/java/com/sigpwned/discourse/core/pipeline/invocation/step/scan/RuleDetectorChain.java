package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import java.util.List;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.util.Maybe;

public class RuleDetectorChain extends Chain<RuleDetector> implements RuleDetector {
  @Override
  public Maybe<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate, InvocationContext context) {
    for (RuleDetector detector : this) {
      Maybe<RuleDetection> maybeRule = detector.detectRule(clazz, syntax, candidate, context);
      if (maybeRule.isDecided())
        return maybeRule;
    }
    return Maybe.maybe();
  }
}