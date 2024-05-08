package com.sigpwned.discourse.core.expr;

import static java.util.Collections.*;

import java.util.Optional;
import java.util.Set;

public class Rule {

  private final Set<String> antecedents;
  private final String consequent;
  private boolean guaranteed;
  private boolean necessary;
  private boolean implied;

  public Rule(Set<String> antecedents, String consequent) {
    this.antecedents = unmodifiableSet(antecedents);
    this.consequent = consequent;
    if (antecedents.isEmpty() && consequent == null) {
      throw new IllegalArgumentException(
          "at least one of antecedents and consequents must not be empty");
    }
    if (antecedents.contains(consequent)) {
      throw new IllegalArgumentException("antecedents and consequent must be disjoint");
    }
  }

  public Set<String> getAntecedents() {
    return antecedents;
  }

  public Optional<String> getConsequent() {
    return Optional.ofNullable(consequent);
  }

  public boolean isGuaranteed() {
    return guaranteed;
  }

  public void setGuaranteed(boolean guaranteed) {
    this.guaranteed = guaranteed;
  }

  public boolean isNecessary() {
    return necessary;
  }

  public void setNecessary(boolean necessary) {
    this.necessary = necessary;
  }

  public boolean isImplied() {
    return implied;
  }

  public void setImplied(boolean implied) {
    this.implied = implied;
  }

  public void clear() {
    setGuaranteed(false);
    setNecessary(false);
    setImplied(false);
  }
}
