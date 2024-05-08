package com.sigpwned.discourse.core.configurable2.syntax;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public record ConfigurableSyntaxCandidate(Object nominated, Type genericType,
    List<Annotation> annotations) {

}
