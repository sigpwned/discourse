package com.sigpwned.discourse.core.module.scan.syntax;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxNominator;
import com.sigpwned.discourse.core.util.Reflection;

/**
 * A {@link SyntaxNominator} that nominates methods with a getter-like signature. This
 * implementation will nominate all non-static methods of the given class and all of its ancestors
 * that have one parameter and a void return type regardless of method name, type, or visibility.
 * 
 * @see Reflection#hasInstanceSetterSignature(Method)
 */
public class SetterMethodSyntaxNominator implements SyntaxNominator {
  @Override
  public List<CandidateSyntax> nominateSyntax(Class<?> clazz) {
    List<CandidateSyntax> result = new ArrayList<>();

    for (Class<?> ancestor = clazz; ancestor != null; ancestor = ancestor.getSuperclass()) {
      for (Method method : ancestor.getDeclaredMethods()) {
        if (Reflection.hasInstanceSetterSignature(method)) {
          result.add(new CandidateSyntax(method, method.getGenericParameterTypes()[0],
              List.of(method.getAnnotations())));
        }
      }
    }

    return result;
  }
}
