package com.sigpwned.discourse.core.invocation.phase.scan.rules;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.NamedSyntax;
import java.util.ArrayList;
import java.util.List;

public class RuleNominatorChain extends Chain<RuleNominator> implements RuleNominator {

  @Override
  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax) {
    List<CandidateRule> result = new ArrayList<>();
    for (RuleNominator nominator : this) {
      result.addAll(nominator.nominateRules(clazz, syntax));
    }
    return result;
  }
}
