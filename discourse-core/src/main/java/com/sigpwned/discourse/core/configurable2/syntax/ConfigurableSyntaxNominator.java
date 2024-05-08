package com.sigpwned.discourse.core.configurable2.syntax;

import java.util.List;

public interface ConfigurableSyntaxNominator {

  public List<ConfigurableSyntaxCandidate> nominateSyntax(Class<?> type);
}
