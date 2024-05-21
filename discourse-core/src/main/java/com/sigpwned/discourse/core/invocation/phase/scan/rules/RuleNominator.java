package com.sigpwned.discourse.core.invocation.phase.scan.rules;

import java.util.List;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;

public interface RuleNominator {

  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax,
      InvocationContext context);
}
