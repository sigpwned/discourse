package com.sigpwned.discourse.core.reflection;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.AccessorNamingScheme;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

public class GetterInstanceAttributeView extends InstanceAttributeView {

  private final Method method;
  private final Visibility visibility;
  private final AccessorNamingScheme naming;
  private final String attributeName;

  public GetterInstanceAttributeView(Method method) {
    this.method = requireNonNull(method);
    if (Modifier.isAbstract(method.getModifiers())) {
      throw new IllegalArgumentException("method must not be abstract");
    }
    if (Modifier.isStatic(method.getModifiers())) {
      throw new IllegalArgumentException("method must not be static");
    }
    if (method.getParameterCount() != 0) {
      throw new IllegalArgumentException("getter methods take no parameters");
    }
    if (method.getReturnType() == void.class) {
      throw new IllegalArgumentException("getter methods must not return void");
    }
    this.visibility = Visibility.fromModifiers(method.getModifiers());
    this.naming = AccessorNamingScheme.detectFromGetterName(method.getName()).orElseThrow(
        () -> new IllegalArgumentException(
            "method name does not conform to any known naming scheme"));
    this.attributeName = naming.parseGetterName(method.getName()).orElseThrow(
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
  public Class<?> getRawType() {
    return getMethod().getReturnType();
  }

  @Override
  public Type getGenericType() {
    return getMethod().getGenericReturnType();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getMethod().getAnnotations());
  }

  @Override
  public boolean canGet() {
    return getVisibility() == Visibility.PUBLIC;
  }

  @Override
  public Object get(Object instance) {
    try {
      return getMethod().invoke(instance);
    } catch (ReflectiveOperationException e) {
      // TODO Better exception
      throw new RuntimeException(e);
    }
  }

  private Method getMethod() {
    return method;
  }
}
