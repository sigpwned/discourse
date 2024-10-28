package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;

public interface RuleEvaluator {
  public Optional<Optional<Object>> run(Map<String, Object> input, NamedRule rule);
}
