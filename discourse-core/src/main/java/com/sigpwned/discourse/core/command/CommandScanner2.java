package com.sigpwned.discourse.core.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CommandScanner2 {
  // We need: syntax, antecedents, consequent
  // We will need to separate annotation sources from value sinks

  public static record ConfigurableComponent(String name, Set<String> antecedents,
      Optional<String> consequent, Object code, Type genericType, List<Annotation> annotations) {

  }

  public static void foo(Object code) {
    if (code instanceof Field field) {
    } else if (code instanceof Method method && !Modifier.isStatic(method.getModifiers())
        && !Modifier.isAbstract(method.getModifiers()) && method.getParameterCount() == 1
        && method.getReturnType() == void.class && method.getName().startsWith("set")) {
      String name = method.getName().substring(3);
      new ConfigurableComponent(name, Set.of("", name), Optional.empty(), method,
          method.getAnnotatedReturnType().getType(),
          List.of(method.getAnnotatedReturnType().getAnnotations()));
    } else if (code instanceof Method method && !Modifier.isStatic(method.getModifiers())
        && !Modifier.isAbstract(method.getModifiers()) && method.getParameterCount() == 0
        && method.getReturnType() != void.class && method.getName().startsWith("get")) {
      String name = method.getName().substring(3);
      // TODO We need to provide the annotation source, but not the value sink
    } else if (code instanceof Constructor<?> constructor) {
      new ConfigurableComponent("", Set.of("", name), Optional.empty(), method,
          method.getAnnotatedReturnType().getType(),
          List.of(method.getAnnotatedReturnType().getAnnotations()));
    } else if (code instanceof Constructor<?> constructor) {
    }
  }


  public static interface CandidateConfigurableComponentScanner {

    public List<Object> scanCandidateConfigurableComponents(Class<?> clazz);
  }

  public static interface ConfigurableComponentFactory {

    public List<ConfigurableComponent> scanConfigurableComponents(Object object);
  }

}
