package com.sigpwned.discourse.core.invocation.phase.scan.model.rules;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import com.sigpwned.discourse.core.invocation.model.RuleDetection;

/**
 * Definition of a configurable rule.
 *
 * @param nominated
 * @param genericType
 * @param annotations
 * @param antecedents
 */
public record DetectedRule(Object nominated, Type genericType, List<Annotation> annotations,
    Set<String> antecedents, boolean hasConsequent) {
  public static DetectedRule fromCandidateAndDetection(CandidateRule candidate,
      RuleDetection detection) {
    return new DetectedRule(candidate.nominated(), candidate.genericType(), candidate.annotations(),
        detection.antecedents(), detection.hasConsequent());
  }
}
