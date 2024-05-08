package com.sigpwned.discourse.core.configurable2.hole;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public record ConfigurableHoleCandidate(Object nominated, Type genericType,
    List<Annotation> annotations) {

}
