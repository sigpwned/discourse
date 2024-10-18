package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * <p>
 * Used when there is a property that cannot be set by any rules. This can happen, for example, when
 * a field is private and has no public setter. This can typically be resolved fairly easily by
 * adding setters for the properties, or by making the fields public if they are not virtual fields.
 * </p>
 */
@SuppressWarnings("serial")
public class InsufficientRulesScanException extends ScanException {
  private final Set<String> givenProperties;
  private final Set<String> unassignedProperties;

  public InsufficientRulesScanException(Class<?> clazz, Set<String> givenProperties,
      Set<String> unassignedProperties) {
    super(clazz,
        format("Command class %s rules given inputs %s do not assign properties %s",
            clazz.getName(), String.join(", ", givenProperties),
            String.join(", ", unassignedProperties)));
    this.givenProperties = unmodifiableSet(givenProperties);
    this.unassignedProperties = unmodifiableSet(unassignedProperties);
  }

  /**
   * @return the givenProperties
   */
  public Set<String> getGivenProperties() {
    return givenProperties;
  }

  /**
   * @return the unassignedProperties
   */
  public Set<String> getUnassignedProperties() {
    return unassignedProperties;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(), String.join(", ", unassignedProperties)};
  }
}
