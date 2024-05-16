package com.sigpwned.discourse.core.invocation.phase.scan.model.rules;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

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

}
