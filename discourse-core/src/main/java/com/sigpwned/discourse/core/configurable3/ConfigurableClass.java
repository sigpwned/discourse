package com.sigpwned.discourse.core.configurable3;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configurable3.rule.NamedRule;
import com.sigpwned.discourse.core.configurable3.syntax.NamedSyntax;
import java.util.List;

public class ConfigurableClass<T> {

  private final Class<T> clazz;
  private final List<NamedSyntax> syntax;
  private final List<NamedRule> rules;

  public ConfigurableClass(Class<T> clazz, List<NamedSyntax> syntax, List<NamedRule> rules) {
    this.clazz = requireNonNull(clazz);
    this.syntax = unmodifiableList(syntax);
    this.rules = unmodifiableList(rules);
    if (rules.isEmpty()) {
      // There must be at least one rule to create the configurable instance
      throw new IllegalArgumentException("rules must not be empty");
    }
  }

  public Class<T> getClazz() {
    return clazz;
  }

  public List<NamedSyntax> getSyntax() {
    return syntax;
  }

  public List<NamedRule> getRules() {
    return rules;
  }
}
