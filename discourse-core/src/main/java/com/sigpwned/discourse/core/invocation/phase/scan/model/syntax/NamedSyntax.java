package com.sigpwned.discourse.core.invocation.phase.scan.model.syntax;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public record NamedSyntax(Object nominated, Type genericType, List<Annotation> annotations,
    boolean required, Map<Object, String> coordinates, String name) {

  public static NamedSyntax fromDetectedSyntax(DetectedSyntax syntax, String name) {
    return new NamedSyntax(syntax.nominated(), syntax.genericType(), syntax.annotations(),
        syntax.required(), syntax.coordinates(), name);
  }
}
