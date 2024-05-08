package com.sigpwned.discourse.core.configurable3;

import com.sigpwned.discourse.core.configurable3.rule.CandidateRule;
import com.sigpwned.discourse.core.configurable3.rule.DetectedRule;
import com.sigpwned.discourse.core.configurable3.rule.NamedRule;
import com.sigpwned.discourse.core.configurable3.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.configurable3.syntax.DetectedSyntax;
import com.sigpwned.discourse.core.configurable3.syntax.NamedSyntax;
import java.util.List;

public class ConfigurableClassScannerListener {

  public void beforeNominateSyntax(Class<?> clazz) {
  }

  public void afterNominateSyntax(Class<?> clazz, List<CandidateSyntax> candidateSyntaxes) {
  }

  public void beforeDetectSyntax(Class<?> clazz, List<CandidateSyntax> candidateSyntaxes) {
  }

  public void afterDetectSyntax(Class<?> clazz, List<CandidateSyntax> candidateSyntaxes,
      List<DetectedSyntax> detectedSyntaxes) {
  }

  public void beforeNameSyntax(Class<?> clazz, List<DetectedSyntax> detectedSyntaxes) {
  }

  public void afterNameSyntax(Class<?> clazz, List<DetectedSyntax> detectedSyntaxes,
      List<NamedSyntax> namedSyntaxes) {
  }

  public void beforeNominateRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes) {
  }

  public void afterNominateRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes,
      List<CandidateRule> candidateRules) {
  }

  public void beforeDetectRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes,
      List<CandidateRule> candidateRules) {
  }

  public void afterDetectRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes,
      List<CandidateRule> candidateRules, List<DetectedRule> detectedRules) {
  }

  public void beforeNameRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes,
      List<DetectedRule> detectedRules) {
  }

  public void afterNameRules(Class<?> clazz, List<NamedSyntax> namedSyntaxes,
      List<DetectedRule> detectedRules, List<NamedRule> namedRules) {
  }

  // TODO How do we structure validating rules?
  // TODO Ensure that required syntax leads to required values
  // TODO Ensure that, if all syntax is provided, all necessary rules are provided
  // TODO Warn if there are any unused rules?
}
