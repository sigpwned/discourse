package com.sigpwned.discourse.core.invocation.phase.scan.model.rules;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Definition of a configurable rule.
 *
 * @param nominated
 * @param genericType
 * @param annotations
 */
public record CandidateRule(Object nominated, Type genericType, List<Annotation> annotations) {

}
