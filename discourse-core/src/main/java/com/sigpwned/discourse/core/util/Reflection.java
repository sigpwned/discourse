package com.sigpwned.discourse.core.util;

import static java.util.Collections.*;

import com.sigpwned.discourse.core.AccessorNamingScheme;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.configurable.parameter.scanner.AttributeConfigurableParameterScanner.Attribute;
import com.sigpwned.discourse.core.reflection.FieldSignature;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public final class Reflection {

  private Reflection() {
  }

  public static Stream<AccessibleObject> stream(Class<?> type) {
    List<AccessibleObject> result = new ArrayList<>();
    for (Class<?> ci = type; ci != null; ci = ci.getSuperclass()) {
      result.addAll(List.of(ci.getDeclaredFields()));
      result.addAll(List.of(ci.getDeclaredConstructors()));
      result.addAll(List.of(ci.getDeclaredMethods()));
    }
    return unmodifiableList(result).stream();
  }


  /**
   * extension hook
   *
   * @param rawType
   * @param objects
   * @return
   */
  protected <T> List<Attribute> scanForAttributes(Class<T> rawType,
      Stream<AccessibleObject> objects) {
    Map<String, List<Method>> setters = new HashMap<>();
    Map<String, List<Method>> getters = new HashMap<>();
    Map<String, List<Field>> fields = new HashMap<>();

    Iterator<AccessibleObject> iterator = objects.iterator();
    while (iterator.hasNext()) {
      AccessibleObject o = iterator.next();
      if (o instanceof Method method) {
        if (hasInstanceGetterSignature(method)) {
          namingScheme.parseGetterName(method.getName()).ifPresent(fieldName -> {
            // This is a (potential) getter for the given field name.
            getters.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(method);
          });
        } else if (hasInstanceSetterSignature(method)) {
          namingScheme.parseSetterName(method.getName()).ifPresent(fieldName -> {
            // This is a (potential) setter for the given field name.
            setters.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(method);
          });
        }
      } else if (o instanceof Field field) {
        if (isMutableInstanceField(field)) {
          // This is a mutable instance field.
          fields.computeIfAbsent(field.getName(), k -> new ArrayList<>()).add(field);
        }
      }
    }

    Set<String> fieldNames = new HashSet<>();
    fieldNames.addAll(setters.keySet());
    fieldNames.addAll(getters.keySet());
    fieldNames.addAll(fields.keySet());

    List<Attribute> attributes = new ArrayList<>();
    for (String fieldName : fieldNames) {
      List<Method> setterList = setters.getOrDefault(fieldName, List.of());
      List<Method> getterList = getters.getOrDefault(fieldName, List.of());
      List<Field> fieldList = fields.getOrDefault(fieldName, List.of());

      if (fieldList.size() > 1) {
        // TODO ConfigurationException for duplicate field?
        // This means we have one field shadowing another. There's no way to resolve this ambiguity.
        // We wouldn't know which getter or setter to use. I think...
        throw new IllegalArgumentException("Duplicate field for " + fieldName);
      }

      List<Annotation> fieldAnnotations = fieldList.stream()
          .flatMap(field -> Stream.of(field.getAnnotations())).filter(
              annotation -> annotation instanceof FlagParameter
                  || annotation instanceof OptionParameter
                  || annotation instanceof EnvironmentParameter
                  || annotation instanceof PositionalParameter
                  || annotation instanceof PropertyParameter).toList();

      List<Annotation> getterAnnotations = getterList.stream()
          .flatMap(getter -> Stream.of(getter.getAnnotations())).filter(
              annotation -> annotation instanceof FlagParameter
                  || annotation instanceof OptionParameter
                  || annotation instanceof EnvironmentParameter
                  || annotation instanceof PositionalParameter
                  || annotation instanceof PropertyParameter).toList();

      List<Annotation> setterAnnotations = setterList.stream()
          .flatMap(setter -> Stream.of(setter.getAnnotations())).filter(
              annotation -> annotation instanceof FlagParameter
                  || annotation instanceof OptionParameter
                  || annotation instanceof EnvironmentParameter
                  || annotation instanceof PositionalParameter
                  || annotation instanceof PropertyParameter).toList();

      if (fieldAnnotations.size() + getterAnnotations.size() + setterAnnotations.size() == 0) {
        // No annotations. This logical field is not configurable.
        continue;
      }
      if (fieldAnnotations.size() + getterAnnotations.size() + setterAnnotations.size() > 1) {
        // TODO ConfigurationException for ambiguous annotations?
        throw new IllegalArgumentException("Ambiguous annotations for " + fieldName);
      }

      Annotation annotation = Stream.of(fieldAnnotations, getterAnnotations, setterAnnotations)
          .flatMap(List::stream).findFirst().orElseThrow();

      Attribute attribute;
      if (fieldList.isEmpty()) {
        // We are a virtual field. Getters and setters only.
        if (setterList.isEmpty() || getterList.isEmpty()) {
          // Welp, we need both a getter and a setter. Otherwise, we can't do anything.
          // TODO Log for missing getter or setter?
          continue;
        }
        Method getter = getterList.get(0);
        Method setter = setterList.get(0);

        if (getter.getReturnType().isAssignableFrom(setter.getParameterTypes()[0])) {
          attribute = Attribute.ofVirtualField(annotation, getter.getGenericReturnType(), fieldName,
              getter, setter);
        } else if (setter.getParameterTypes()[0].isAssignableFrom(getter.getReturnType())) {
          attribute = Attribute.ofVirtualField(annotation, setter.getGenericParameterTypes()[0],
              fieldName, getter, setter);
        } else {
          // TODO ConfigurationException for incompatible getter and setter?
          throw new IllegalArgumentException(
              "Incompatible getter and setter for virtual field " + fieldName);
        }
      } else {
        // We are a concrete field. Getters and setters are optional.
        Field field = fieldList.get(0);
        Method maybeGetter = getterList.stream().findFirst().orElse(null);
        Method maybeSetter = setterList.stream().findFirst().orElse(null);

        if (maybeGetter != null && !maybeGetter.getReturnType().isAssignableFrom(field.getType())) {
          // TODO ConfigurationException for incompatible getter?
          throw new IllegalArgumentException("Incompatible getter for " + fieldName);
        }
        if (maybeSetter != null && !maybeSetter.getParameterTypes()[0].isAssignableFrom(
            field.getType())) {
          // TODO ConfigurationException for incompatible setter?
          throw new IllegalArgumentException("Incompatible setter for " + fieldName);
        }

        if (maybeGetter != null && maybeSetter != null) {
          attribute = Attribute.ofConcreteFieldWithAccessibleAccessors(annotation,
              field.getGenericType(), fieldName, field, maybeGetter, maybeSetter);
        } else {
          attribute = Attribute.ofConcreteAccessibleField(annotation, field.getGenericType(),
              fieldName, field);
        }
      }

      attributes.add(attribute);
    }

    return unmodifiableList(attributes);
  }

  /**
   * <p>
   * Returns {@code true} if the given constructor is a default constructor. That is:
   * </p>
   *
   * <ul>
   *   <li>It has no parameters</li>
   *   <li>It is public</li>
   * </ul>
   *
   * @param constructor the constructor to check
   * @return {@code true} if the given constructor is a default constructor
   */
  private static boolean isAccessibleConstructor(Constructor<?> constructor) {
    return Modifier.isPublic(constructor.getModifiers());
  }

  /**
   * <p>
   * Returns {@code true} if the given method is a factory method. That is:
   * </p>
   *
   * <ul>
   *   <li>It returns something other than {@code void}</li>
   *   <li>It is public</li>
   *   <li>It is static</li>
   * </ul>
   *
   * @param method the method to check
   * @return {@code true} if the given method is a factory method
   */
  private static boolean isFactoryMethod(Method method) {
    return !void.class.equals(method.getReturnType()) && Modifier.isPublic(method.getModifiers())
        && Modifier.isStatic(method.getModifiers());
  }

  /**
   * <p>
   * Returns {@code true} if the given field is a mutable instance field. That is:
   * </p>
   *
   * <ul>
   *   <li>It is not static</li>
   *   <li>It is not final</li>
   * </ul>
   *
   * @param field the field to check
   * @return {@code true} if the given field is an instance field that is mutable
   */
  private static boolean isMutableInstanceField(Field field) {
    return !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers());
  }

  /**
   * <p>
   * Returns {@code true} if the given method has the signature of a getter method. That is:
   * </p>
   *
   * <ul>
   *   <li>It has no parameters</li>
   *   <li>It returns something other than {@code void}</li>
   *   <li>It is public</li>
   *   <li>It is not static</li>
   * </ul>
   *
   * <p>
   *   This method does not check the name of the method.
   * </p>
   *
   * @param method the method to check
   * @return {@code true} if the given method has the signature of a getter method
   */
  public static boolean hasInstanceGetterSignature(Method method) {
    return method.getParameterCount() == 0 && !void.class.equals(method.getReturnType())
        && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers());
  }

  public static boolean isInstanceGetterForField(Method method, FieldSignature field,
      AccessorNamingScheme naming) {
    return getFieldSignatureForInstanceGetter(method, naming).map(x -> x.isAssignableFrom(field))
        .orElse(false);
  }

  public static Optional<FieldSignature> getFieldSignatureForInstanceGetter(Method method,
      AccessorNamingScheme naming) {
    if (hasInstanceGetterSignature(method)) {
      return naming.parseGetterName(method.getName())
          .map(name -> new FieldSignature(method.getReturnType(), name));
    }
    return Optional.empty();
  }

  /**
   * <p>
   * Returns {@code true} if the given method has the signature of a setter method. That is:
   * </p>
   *
   * <ul>
   *   <li>It has a single parameter</li>
   *   <li>It returns {@code void}</li>
   *   <li>It is public</li>
   *   <li>It is not static</li>
   *   <li>It is named like a setter for some field name</li>
   * </ul>
   *
   * <p>
   *   This method does not check the name of the method.
   * </p>
   *
   * @param method the method to check
   * @return {@code true} if the given method has the signature of a setter method
   */
  public static boolean hasInstanceSetterSignature(Method method) {
    return method.getParameterCount() == 1 && void.class.equals(method.getReturnType())
        && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers());
  }

  public static boolean isInstanceSetterForField(Method method, FieldSignature field,
      AccessorNamingScheme naming) {
    return getFieldSignatureForInstanceSetter(method, naming).map(field::isAssignableFrom)
        .orElse(false);
  }

  public static Optional<FieldSignature> getFieldSignatureForInstanceSetter(Method method,
      AccessorNamingScheme naming) {
    if (hasInstanceSetterSignature(method)) {
      return naming.parseSetterName(method.getName())
          .map(name -> new FieldSignature(method.getParameters()[0].getType(), name));
    }
    return Optional.empty();
  }
}
