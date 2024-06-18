package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * <p>
 * Used when there is no one set of rules that provably evaluates everything that all other sets of
 * rules evaluate. This is a problem because it means that the rules are divergent, which is to say
 * that the choice of rules affects the output. This is typically the result of a command having
 * multiple properties that can be set only by constructors or factory methods that take multiple
 * arguments.
 * </p>
 * 
 * <p>
 * For example, the following class has divergent rules:
 * </p>
 * 
 * <pre>
 * &#64;Configurable
 * public class DivergentRules {
 *   &#64;DiscourseCreator
 *   public DivergentRules(&#64;DiscourseProperty("a") String a) {}
 * 
 *   &#64;DiscourseCreator
 *   public DivergentRules(&#64;DiscourseProperty("b") String b) {}
 * }
 * </pre>
 * 
 * <p>
 * The underlying cause of the divergence is that the properties {@code a} and {@code b} can only be
 * set by constructors, but no one constructor can set both properties. This means that the choice
 * of constructor affects the output.
 * </p>
 * 
 * <p>
 * This can typically be resolved fairly easily by adding a constructor that takes all divergent
 * properties, adding setters for the properties, or by making the fields public if they are not
 * virtual fields.
 * </p>
 */
@SuppressWarnings("serial")
public class DivergentRulesScanException extends ScanException {
  private final Set<String> divergentAntecedents;

  public DivergentRulesScanException(Class<?> clazz, Set<String> divergentAntecedents) {
    super(clazz, format("Command class %s rules diverge on the antecedents %s", clazz.getName(),
        String.join(", ", divergentAntecedents)));
    this.divergentAntecedents = unmodifiableSet(divergentAntecedents);
  }

  /**
   * @return the divergentAntecedents
   */
  public Set<String> getDivergentAntecedents() {
    return divergentAntecedents;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(), String.join(", ", divergentAntecedents)};
  }
}
