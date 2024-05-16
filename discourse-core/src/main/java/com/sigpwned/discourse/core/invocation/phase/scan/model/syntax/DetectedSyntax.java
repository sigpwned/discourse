package com.sigpwned.discourse.core.invocation.phase.scan.model.syntax;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public record DetectedSyntax(Object nominated, Type genericType, List<Annotation> annotations,
    boolean required, Map<Object, String> coordinates) {

}
