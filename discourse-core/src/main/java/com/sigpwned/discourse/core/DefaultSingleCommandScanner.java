/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class DefaultSingleCommandScanner {

  protected static enum AccessorNamingScheme {
    BEAN {
      @Override
      public Optional<String> parseGetterName(String methodName) {
        if (methodName.startsWith("get") && methodName.length() > 3 && Character.isUpperCase(
            methodName.charAt(3))) {
          return Optional.of(methodName.substring(3, 4).toLowerCase() + methodName.substring(4));
        } else {
          return Optional.empty();
        }
      }

      @Override
      public Optional<String> parseSetterName(String methodName) {
        if (methodName.startsWith("set") && methodName.length() > 3 && Character.isUpperCase(
            methodName.charAt(3))) {
          return Optional.of(methodName.substring(3, 4).toLowerCase() + methodName.substring(4));
        } else {
          return Optional.empty();
        }
      }

      @Override
      public String computeGetterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      }

      @Override
      public String computeSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      }
    }, RECORD {
      @Override
      public Optional<String> parseGetterName(String methodName) {
        if (Character.isLowerCase(methodName.charAt(0))) {
          return Optional.of(methodName);
        } else {
          return Optional.empty();
        }
      }

      @Override
      public Optional<String> parseSetterName(String methodName) {
        if (Character.isLowerCase(methodName.charAt(0))) {
          return Optional.of(methodName);
        } else {
          return Optional.empty();
        }
      }

      @Override
      public String computeGetterName(String fieldName) {
        return fieldName;
      }

      @Override
      public String computeSetterName(String fieldName) {
        return fieldName;
      }
    };

    public abstract Optional<String> parseGetterName(String methodName);

    public abstract Optional<String> parseSetterName(String methodName);

    public abstract String computeGetterName(String fieldName);

    public abstract String computeSetterName(String fieldName);
  }

  protected static record Creator(Optional<Constructor<?>> constructor, Optional<Method> factory) {

    public static Creator ofConstructor(Constructor<?> constructor) {
      return new Creator(Optional.of(constructor), Optional.empty());
    }

    public static Creator ofFactoryMethod(Method factory) {
      return new Creator(Optional.empty(), Optional.of(factory));
    }

    public Creator {
      constructor = requireNonNull(constructor);
      factory = requireNonNull(factory);
      if (constructor.isEmpty() && factory.isEmpty()) {
        throw new IllegalArgumentException("Either constructor or factory must be provided");
      }
    }
  }

  protected static record Attribute(Annotation annotation, Type genericType, String name,
      Optional<Field> field, Optional<Method> getter, Optional<Method> setter) {

    public static Attribute ofConcreteAccessibleField(Annotation annotation, Type genericType,
        String name, Field field) {
      if (!Modifier.isPublic(field.getModifiers())) {
        throw new IllegalArgumentException("Field must be public");
      }
      return new Attribute(annotation, genericType, name, Optional.of(field), Optional.empty(),
          Optional.empty());
    }

    public static Attribute ofConcreteFieldWithAccessibleAccessors(Annotation annotation,
        Type genericType, String name, Field field, Method getter, Method setter) {
      if (!Modifier.isPublic(getter.getModifiers()) || !Modifier.isPublic(setter.getModifiers())) {
        throw new IllegalArgumentException("Field getter and setter must be public");
      }
      return new Attribute(annotation, genericType, name, Optional.of(field), Optional.of(getter),
          Optional.of(setter));
    }

    public static Attribute ofVirtualField(Annotation annotation, Type genericType, String name,
        Method getter, Method setter) {
      if (!Modifier.isPublic(getter.getModifiers()) || !Modifier.isPublic(setter.getModifiers())) {
        throw new IllegalArgumentException("Virtual field getter and setter must be public");
      }
      return new Attribute(annotation, genericType, name, Optional.empty(), Optional.of(getter),
          Optional.of(setter));
    }

    public Attribute {
      annotation = requireNonNull(annotation);
      genericType = requireNonNull(genericType);
      name = requireNonNull(name);
      field = requireNonNull(field);
      getter = requireNonNull(getter);
      setter = requireNonNull(setter);
      if (field.isEmpty() && (getter.isEmpty() || setter.isEmpty())) {
        throw new IllegalArgumentException("Either field or getter and setter must be provided");
      }
    }
  }


  private AccessorNamingScheme namingScheme;

  public <T> SingleCommand<T> fit(Class<T> rawType, Stream<AccessibleObject> objects) {
    List<AccessibleObject> objectsList = objects.toList();

    List<Attribute> attributes = scanForAttributes(rawType, objectsList.stream());

    List<Creator> creators = scanForCreators(rawType, objectsList.stream());

    Creator creator = chooseCreator(creators).orElseThrow(() -> {
      // TODO ConfigurationException for no creator?
      return new IllegalArgumentException("No creator found");
    });

    return new SingleCommand<>(rawType, attributes, creator);
  }

  /**
   * extension hook
   *
   * @param rawType the raw type to scan
   * @return a list of configuration parameters that can be used to configure the given raw type
   */
  protected <T> List<Creator> scanForCreators(Class<T> rawType, Stream<AccessibleObject> objects) {
    List<Creator> result = new ArrayList<>();
    Iterator<AccessibleObject> iterator = objects.iterator();
    while (iterator.hasNext()) {
      AccessibleObject o = iterator.next();
      if (o instanceof Constructor<?> constructor) {
        if (isAccessibleConstructor(constructor) && constructor.getDeclaringClass()
            .equals(rawType)) {
          result.add(Creator.ofConstructor(constructor));
        }
      } else if (o instanceof Method method) {
        if (isFactoryMethod(method) && rawType.isAssignableFrom(method.getReturnType())) {
          result.add(Creator.ofFactoryMethod(method));
        }
      }
    }
    return unmodifiableList(result);
  }

  /**
   * extension hook. Chooses the default constructor.
   *
   * @param creators
   * @return
   */
  protected Optional<Creator> chooseCreator(List<Creator> creators) {
    return creators.stream().filter(creator -> creator.constructor().isPresent())
        .filter(creator -> creator.constructor().orElseThrow().getParameterCount() == 0)
        .findFirst();
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
  private static boolean hasInstanceGetterSignature(Method method) {
    return method.getParameterCount() == 0 && !void.class.equals(method.getReturnType())
        && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers());
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
  private static boolean hasInstanceSetterSignature(Method method) {
    return method.getParameterCount() == 1 && void.class.equals(method.getReturnType())
        && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers());
  }
}
