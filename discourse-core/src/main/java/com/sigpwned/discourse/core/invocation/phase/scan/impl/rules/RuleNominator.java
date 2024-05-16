package com.sigpwned.discourse.core.invocation.phase.scan.impl.rules;

import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.syntax.NamedSyntax;
import java.util.List;

public interface RuleNominator {

  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax);
}
