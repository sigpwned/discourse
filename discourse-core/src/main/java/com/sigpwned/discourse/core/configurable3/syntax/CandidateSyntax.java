package com.sigpwned.discourse.core.configurable3.syntax;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public record CandidateSyntax(Object nominated, Type genericType, List<Annotation> annotations) {

}
