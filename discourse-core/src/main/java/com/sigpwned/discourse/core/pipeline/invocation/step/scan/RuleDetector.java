package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import java.util.List;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.RuleDetection;
import com.sigpwned.discourse.core.util.Maybe;

public interface RuleDetector {
  public Maybe<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
      CandidateRule candidate, InvocationContext context);
}
