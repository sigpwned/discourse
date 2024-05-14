package com.sigpwned.discourse.core.invocation.phase.scan.impl;

import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.CandidateRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.DetectedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.NamedRule;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.DetectedSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.syntax.NamedSyntax;
import java.util.List;

public interface ConfigurableClassScannerListener {

  default void beforeNominateSyntax(Class<?> clazz) {
  }

  default void afterNominateSyntax(Class<?> clazz, List<CandidateSyntax> candidateSyntaxes) {
  }

  default void beforeDetectSyntax(Class<?> clazz, List<CandidateSyntax> candidateSyntaxes) {
  }

  default void afterDetectSyntax(Class<?> clazz, List<CandidateSyntax> candidateSyntaxes,
      List<DetectedSyntax> detectedSyntaxes) {
  }

  default void beforeNameSyntax(Class<?> clazz, List<DetectedSyntax> detectedSyntaxes) {
  }

  default void afterNameSyntax(Class<?> clazz, List<DetectedSyntax> detectedSyntaxes,
      List<NamedSyntax> namedSyntaxes) {
  }

  default void beforeNominateRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes) {
  }

  default void afterNominateRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes,
      List<CandidateRule> candidateRules) {
  }

  default void beforeDetectRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes,
      List<CandidateRule> candidateRules) {
  }

  default void afterDetectRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes,
      List<CandidateRule> candidateRules, List<DetectedRule> detectedRules) {
  }

  default void beforeNameRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes,
      List<DetectedRule> detectedRules) {
  }

  default void afterNameRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes,
      List<DetectedRule> detectedRules, List<NamedRule> namedRules) {
  }

  // TODO How do we structure validating rules?
  // TODO Ensure that required syntax leads to required values
  // TODO Ensure that, if all syntax is provided, all necessary rules are provided
  // TODO Warn if there are any unused rules?
}
