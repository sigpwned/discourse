package com.sigpwned.discourse.core.configurable2.syntax;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public record ConfigurableSyntax(Object nominated, Type genericType, List<Annotation> annotations,
    Map<Object, String> coordinates) {

}
