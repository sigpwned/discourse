package com.sigpwned.discourse.core.module.scan.syntax.nominate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxNominator;

/**
 * A {@link SyntaxNominator} that nominates fields. This nominator will nominate all non-static
 * fields of the given class and all of its ancestors regardless of field name, type, or visibility.
 */
public class FieldSyntaxNominator implements SyntaxNominator {
  public static final FieldSyntaxNominator INSTANCE = new FieldSyntaxNominator();

  @Override
  public List<CandidateSyntax> nominateSyntax(Class<?> clazz, InvocationContext context) {
    List<CandidateSyntax> result = new ArrayList<>();

    for (Class<?> ancestor = clazz; ancestor != null; ancestor = ancestor.getSuperclass()) {
      for (Field field : ancestor.getDeclaredFields()) {
        if (!Modifier.isStatic(field.getModifiers())) {
          result.add(
              new CandidateSyntax(field, field.getGenericType(), List.of(field.getAnnotations())));
        }
      }
    }

    return result;
  }
}
