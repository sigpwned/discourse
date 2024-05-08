package com.sigpwned.discourse.core.configurable3.syntax;

import java.util.List;

public interface SyntaxNominator {

  public List<CandidateSyntax> nominateSyntax(Class<?> clazz);
}
