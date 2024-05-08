package com.sigpwned.discourse.core.configurable3.rule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Definition of a configurable rule.
 *
 * @param nominated
 * @param genericType
 * @param annotations
 * @param antecedents
 * @param consequent
 */
public record NamedRule(Object nominated, Type genericType, List<Annotation> annotations,
    Set<String> antecedents, Optional<String> consequent) {

}
