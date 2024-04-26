package com.sigpwned.discourse.core.configurable.parameter.scanner;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.sigpwned.discourse.core.AccessorNamingScheme;
import com.sigpwned.discourse.core.DefaultSingleCommandScanner.Creator;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.util.ParameterAnnotations;
import com.sigpwned.discourse.core.util.Streams;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AttributeConfigurableParameterScanner {

  protected static record Attribute(Annotation annotation, Type genericType, String name,
      Optional<Field> field, Optional<Method> getter, Optional<Method> setter) {

    public static Optional<Attribute> fromAttributeBucket(AttributeBucket bucket) {
      Map<AccessibleObject, List<Annotation>> bucketParameterAnnotations = bucket.stream().filter(
              a -> Stream.of(a.getAnnotations()).anyMatch(ParameterAnnotations::isParameterAnnotation))
          .collect(groupingBy(a -> a, Collectors.flatMapping(a -> Stream.of(a.getAnnotations())
              .filter(ParameterAnnotations::isParameterAnnotation), toList())));
      if (bucketParameterAnnotations.size() == 0) {
        // No parameter annotation. This field is not configurable. Skip it.
        return Optional.empty();
      }
      if (bucketParameterAnnotations.size() > 1) {
        // TODO better exception
        // ConfigurationException for annotations on multiple fields/methods?
        throw new IllegalArgumentException("Ambiguous annotations for " + bucket.fieldName);
      }

      List<Annotation> entryParameterAnnotations = bucketParameterAnnotations.values().iterator()
          .next();
      if (entryParameterAnnotations.size() > 1) {
        // TODO Better exception
        // ConfigurationException for multiple annotations on single field/method?
        throw new IllegalArgumentException("Ambiguous annotations for " + bucket.fieldName);
      }

      // Right, here's our parameter annotation, and the entry it's on.
      AccessibleObject parameterEntry = bucketParameterAnnotations.keySet().iterator().next();
      Annotation parameterAnnotation = entryParameterAnnotations.get(0);

      // If there are multiple fields, we have a problem.
      if (bucket.fields.size() > 1) {
        // TODO Better exception
        // We could probably figure this out, but it could cause really subtle bugs.
        throw new IllegalArgumentException("Duplicate field for " + bucket.fieldName);
      }

      Class<?> attributeType;

      // If the field is public and annotated, use that directly.
      if (parameterEntry instanceof Field field && Modifier.isPublic(field.getModifiers())) {
        // Square deal. We're a public annotated field.
        return Optional.of(
            Attribute.ofConcreteAccessibleField(parameterAnnotation, field.getGenericType(),
                field.getName(), field));
      }

      if (parameterEntry instanceof Method method && Modifier.isPublic(method.getModifiers())
          && bucket.isSetter(method)) {
        // We're an annotated, public setter.
        return Optional.of(
            Attribute.ofConcreteAccessibleField(parameterAnnotation, method.getGenericReturnType(),
                method.getName(), method));
      }

      if (!bucket.fields.isEmpty()) {
        Field field = bucket.fields.get(0);
        if (Modifier.isPublic(field.getModifiers()) && Stream.of(field.getAnnotations())
            .anyMatch(ParameterAnnotations::isParameterAnnotation)) {
          return Optional.of(
              Attribute.ofConcreteAccessibleField(parameterAnnotation, field.getGenericType(),
                  field.getName(), field));
        }
      }

      if (!bucket.fields.isEmpty()) {
        Field field = bucket.fields.get(0);
        if (Modifier.isPublic(field.getModifiers()) && Stream.of(field.getAnnotations())
            .anyMatch(ParameterAnnotations::isParameterAnnotation)) {
          // Square deal. We're a public annotated field.
          return Optional.of(
              Attribute.ofConcreteAccessibleField(parameterAnnotation, field.getGenericType(),
                  field.getName(), field));
        }
      }

      Optional<Method> maybeSetter = bucket.setters.stream().findFirst();
      if (maybeSetter.isPresent()) {
        if (!bucket.fields.isEmpty()) {
          Field
        } Method setter = maybeSetter.get();
        if (setter.getParameters()[0]) {
          return new Attribute(parameterAnnotation, null, bucket.getFieldName(), null, null, null);
        }
      }

      // TODO Better exception
      // What should we do if there is a field with an annotation but no way to set?
      throw new

          IllegalArgumentException("No way to set field " + bucket.fieldName);
    }

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
      if (field.isEmpty() && setter.isEmpty()) {
        throw new IllegalArgumentException("Either field or setter must be provided");
      }
    }
  }

  protected static class AttributeBuckets {

    private final AccessorNamingScheme naming;
    private final Map<String, AttributeBucket> buckets;

    public AttributeBuckets(AccessorNamingScheme naming) {
      this.buckets = new HashMap<>();
    }

    public AccessorNamingScheme getNaming() {
      return naming;
    }

    public void addField(Field field) {
      String fieldName = field.getName();
      AttributeBucket bucket = buckets.computeIfAbsent(fieldName,
          n -> new AttributeBucket(getNaming(), n));
      bucket.addField(field);
    }

    public void addGetter(Method getter) {
      String fieldName = getter.getName();
      AttributeBucket bucket = buckets.computeIfAbsent(fieldName,
          n -> new AttributeBucket(getNaming(), n));
      bucket.addGetter(getter);
    }

    public void addSetter(Method setter) {
      String fieldName = setter.getName();
      AttributeBucket bucket = buckets.computeIfAbsent(fieldName,
          n -> new AttributeBucket(getNaming(), n));
      bucket.addSetter(setter);
    }

    public void merge(AttributeBuckets that) {
      if (!this.getNaming().equals(that.getNaming())) {
        throw new IllegalArgumentException("Naming schemes must match");
      }
      for (Map.Entry<String, AttributeBucket> entry : that.buckets.entrySet()) {
        String fieldName = entry.getKey();
        AttributeBucket thisBucket = buckets.computeIfAbsent(fieldName,
            n -> new AttributeBucket(getNaming(), n));
        AttributeBucket thatBucket = entry.getValue();
        thisBucket.merge(thatBucket);
      }
    }

    public List<Attribute> toAttributes() {
      return buckets.values().stream().map(bucket -> {
        Optional<Annotation> parameterAnnotation = bucket.getParameterAnnotation();
        if (parameterAnnotation.isEmpty()) {
          return null;
        }
        return new Attribute(parameterAnnotation.get(), null, bucket.getFieldName(), null, null,
            null);
      }).toList();
    }
  }

  protected static class AttributeBucket {

    private final AccessorNamingScheme naming;
    private final String fieldName;
    private final List<Field> fields;
    private final List<Method> getters;
    private final List<Method> setters;

    public AttributeBucket(AccessorNamingScheme naming, String fieldName) {
      this.naming = requireNonNull(naming);
      this.fieldName = requireNonNull(fieldName);
      this.fields = new ArrayList<>();
      this.getters = new ArrayList<>();
      this.setters = new ArrayList<>();
    }

    public AccessorNamingScheme getNaming() {
      return naming;
    }

    public String getFieldName() {
      return fieldName;
    }

    public void addField(Field field) {
      if (!field.getName().equals(getFieldName())) {
        throw new IllegalArgumentException(
            "Cannot add field with name %s to bucket for %s".formatted(field.getName(),
                getFieldName()));
      }
      if (Modifier.isStatic(field.getModifiers())) {
        throw new IllegalArgumentException(
            "Cannot add static field to bucket for %s".formatted(getFieldName()));
      }
      if (Modifier.isFinal(field.getModifiers())) {
        throw new IllegalArgumentException(
            "Cannot add final field to bucket for %s".formatted(getFieldName()));
      }
      fields.add(field);
    }

    public void addGetter(Method getter) {
      getNaming().parseGetterName(getter.getName()).ifPresentOrElse(fieldName -> {
        if (!fieldName.equals(getFieldName())) {
          throw new IllegalArgumentException(
              "Cannot add getter for field %s to bucket for %s".formatted(fieldName,
                  getFieldName()));
        }
      }, () -> {
        throw new IllegalArgumentException(
            "Cannot add non-getter method %s to bucket for %s".formatted(getter.getName(),
                getFieldName()));
      });
      if (Modifier.isStatic(getter.getModifiers())) {
        throw new IllegalArgumentException(
            "Cannot add static getter to bucket for %s".formatted(getFieldName()));
      }
      if (Modifier.isAbstract(getter.getModifiers())) {
        throw new IllegalArgumentException(
            "Cannot add abstract getter to bucket for %s".formatted(getFieldName()));
      }
      getters.add(getter);
    }

    public void addSetter(Method setter) {
      getNaming().parseSetterName(setter.getName()).ifPresentOrElse(fieldName -> {
        if (!fieldName.equals(getFieldName())) {
          throw new IllegalArgumentException(
              "Cannot add setter for field %s to bucket for %s".formatted(fieldName,
                  getFieldName()));
        }
      }, () -> {
        throw new IllegalArgumentException(
            "Cannot add non-setter method %s to bucket for %s".formatted(setter.getName(),
                getFieldName()));
      });
      if (Modifier.isStatic(setter.getModifiers())) {
        throw new IllegalArgumentException(
            "Cannot add static setter to bucket for %s".formatted(getFieldName()));
      }
      if (Modifier.isAbstract(setter.getModifiers())) {
        throw new IllegalArgumentException(
            "Cannot add abstract setter to bucket for %s".formatted(getFieldName()));
      }
      setters.add(setter);
    }

    public void merge(AttributeBucket that) {
      if (!this.getFieldName().equals(that.getFieldName())) {
        throw new IllegalArgumentException("Field names must match");
      }
      this.fields.addAll(that.fields);
      this.getters.addAll(that.getters);
      this.setters.addAll(that.setters);
    }

    public boolean isGetter(Method method) {
      return getters.contains(method);
    }

    public boolean isSetter(Method method) {
      return setters.contains(method);
    }

    public Stream<AccessibleObject> stream() {
      return Streams.concat(fields.stream(), getters.stream(), setters.stream());
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
