package com.sigpwned.discourse.core.invocation.phase.scan.impl.rules;

import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.NamedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.CandidateRule;
import java.util.List;

public interface RuleNominator {

  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax);
}
