package com.sigpwned.discourse.core.invocation.phase.scan.rules;

import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;

public interface RuleEvaluator {

  /**
   * <p>
   * Attempts to run the given rule on the given input. If the rule is not applicable to the input,
   * an empty optional is returned. If the rule is applicable, an optional containing the result of
   * the rule is returned, which is itself an optional. This leads to three possible outcomes:
   * </p>
   * 
   * <ol>
   * <li>The rule is not applicable to the input: An empty optional is returned.</li>
   * <li>The rule is applicable to the input, and evaluating the rule produced a value: An optional
   * containing an optional containing the result of the evaluation is returned.</li>
   * <li>The rule is applicable to the input, but evaluating the rule produced no value: An optional
   * containing an empty optional is returned.</li>
   * </ol>
   * 
   * @param input The input to evaluate the rule on.
   * @param rule The rule to evaluate.
   * @return An optional containing the result of the rule evaluation, or an empty optional if the
   *         rule is not applicable.
   */
  public Optional<Optional<Object>> run(Map<String, Object> input, NamedRule rule);
}
