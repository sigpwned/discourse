package com.sigpwned.discourse.core.configurable3.rule;

import com.sigpwned.discourse.core.configurable3.syntax.NamedSyntax;
import java.util.List;

public interface RuleNominator {

  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax);
}
