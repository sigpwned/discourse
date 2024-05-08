package com.sigpwned.discourse.core.configurable3.syntax;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public record NamedSyntax(Object nominated, Type genericType, List<Annotation> annotations,
    boolean required, Map<Object, String> coordinates, String name) {

}
