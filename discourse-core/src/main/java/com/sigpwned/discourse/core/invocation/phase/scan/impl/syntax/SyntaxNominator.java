package com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax;

import java.util.List;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.syntax.CandidateSyntax;

public interface SyntaxNominator {

  public List<CandidateSyntax> nominateSyntax(Class<?> clazz);
}
