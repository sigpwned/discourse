package com.sigpwned.discourse.core.reflection;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.AccessorNamingScheme;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

public class SetterInstanceAttributeView extends InstanceAttributeView {

  private final Method method;
  private final Visibility visibility;
  private final AccessorNamingScheme naming;
  private final String attributeName;

  public SetterInstanceAttributeView(Method method) {
    this.method = requireNonNull(method);
    if (Modifier.isAbstract(method.getModifiers())) {
      throw new IllegalArgumentException("method must not be abstract");
    }
    if (Modifier.isStatic(method.getModifiers())) {
      throw new IllegalArgumentException("method must not be static");
    }
    if (method.getParameterCount() != 1) {
      throw new IllegalArgumentException("setter methods must take exactly one parameter");
    }
    if (method.getReturnType() != void.class) {
      throw new IllegalArgumentException("setter methods must return void");
    }
    this.visibility = Visibility.fromModifiers(method.getModifiers());
    this.naming = AccessorNamingScheme.detectFromSetterName(method.getName()).orElseThrow(
        () -> new IllegalArgumentException(
            "method name does not conform to any known naming scheme"));
    this.attributeName = naming.parseSetterName(method.getName()).orElseThrow(
        () -> new AssertionError(
            "getter method name %s does not conform to selected naming scheme %s".formatted(
                method.getName(), naming)));
  }

  @Override
  public Visibility getVisibility() {
    return visibility;
  }

  public AccessorNamingScheme getNaming() {
    return naming;
  }

  public String getMethodName() {
    return getMethod().getName();
  }

  @Override
  public String getAttributeName() {
    return attributeName;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getMethod().getAnnotations());
  }

  @Override
  public Class<?> getRawType() {
    return getMethod().getReturnType();
  }

  @Override
  public Type getGenericType() {
    return getMethod().getGenericReturnType();
  }

  @Override
  public boolean canSet() {
    return getVisibility() == Visibility.PUBLIC;
  }

  @Override
  public void set(Object instance, Object value) {
    try {
      getMethod().invoke(instance, value);
    } catch (ReflectiveOperationException e) {
      // TODO Better exception
      throw new RuntimeException(e);
    }
  }

  private Method getMethod() {
    return method;
  }
}
