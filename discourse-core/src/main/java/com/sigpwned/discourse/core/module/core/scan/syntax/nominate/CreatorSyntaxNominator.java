package com.sigpwned.discourse.core.module.core.scan.syntax.nominate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.annotation.DiscourseCreator;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.util.Reflection;

public class CreatorSyntaxNominator implements SyntaxNominator {
  public static final CreatorSyntaxNominator INSTANCE = new CreatorSyntaxNominator();

  @Override
  public List<CandidateSyntax> nominateSyntax(Class<?> clazz, InvocationContext context) {
    List<CandidateSyntax> result = new ArrayList<>();

    for (Constructor<?> constructor : clazz.getConstructors()) {
      // If it's not annotated, we don't want it
      DiscourseCreator annotation = constructor.getAnnotation(DiscourseCreator.class);
      if (annotation == null)
        continue;

      // If it's the default constructor, that's already handled
      if (Reflection.hasDefaultConstructorSignature(constructor))
        continue;

      for (int i = 0; i < constructor.getParameterCount(); i++) {
        Parameter parameter = constructor.getParameters()[i];
        result.add(new CandidateSyntax("constructor", parameter, parameter.getParameterizedType(),
            List.of(parameter.getAnnotations())));
      }
    }

    for (Class<?> ancestor = clazz; ancestor != null; ancestor = ancestor.getSuperclass()) {
      for (Method method : ancestor.getDeclaredMethods()) {
        DiscourseCreator annotation = method.getAnnotation(DiscourseCreator.class);
        if (annotation == null)
          continue;

        // TODO log warning
        if (!Reflection.hasFactoryMethodSignature(method))
          continue;

        // TODO log warning
        if (!Modifier.isPublic(method.getModifiers()))
          continue;

        for (int i = 0; i < method.getParameterCount(); i++) {
          Parameter parameter = method.getParameters()[i];
          result.add(new CandidateSyntax(method.getName(), parameter,
              parameter.getParameterizedType(), List.of(parameter.getAnnotations())));
        }
      }
    }

    return result;
  }
}
