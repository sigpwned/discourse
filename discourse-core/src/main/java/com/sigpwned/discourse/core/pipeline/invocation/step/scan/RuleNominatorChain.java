package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;

public class RuleNominatorChain extends Chain<RuleNominator> implements RuleNominator {

  @Override
  public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax,
      InvocationContext context) {
    List<CandidateRule> result = new ArrayList<>();
    for (RuleNominator nominator : this) {
      List<CandidateRule> moreRules = nominator.nominateRules(clazz, syntax, context);
      result.addAll(moreRules);
    }
    return unmodifiableList(result);
  }
}
