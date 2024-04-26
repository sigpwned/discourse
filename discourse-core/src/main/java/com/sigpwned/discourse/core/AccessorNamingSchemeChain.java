package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.util.Chains;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

public class AccessorNamingSchemeChain extends Chain<AccessorNamingScheme> {

  public Optional<String> getAttributeGetterName(String methodName, List<Annotation> annotations) {
    return Chains.stream(this)
        .flatMap(link -> link.getAttributeGetterName(methodName, annotations).stream()).findFirst();
  }

  public Optional<String> getAttributeSetterName(String methodName, List<Annotation> annotations) {
    return Chains.stream(this)
        .flatMap(link -> link.getAttributeSetterName(methodName, annotations).stream()).findFirst();
  }

  public Optional<String> getAttributeFieldName(String fieldName, List<Annotation> annotations) {
    return Chains.stream(this)
        .flatMap(link -> link.getAttributeFieldName(fieldName, annotations).stream()).findFirst();
  }

  public Optional<String> getAttributeConstructorParameterName(
      List<Annotation> constructorAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    return Chains.stream(this).flatMap(
        link -> link.getAttributeConstructorParameterName(constructorAnnotations, parameterName,
            parameterAnnotations).stream()).findFirst();
  }

  public Optional<String> getAttributeFactoryMethodParameterName(String methodName,
      List<Annotation> methodAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    return Chains.stream(this).flatMap(
        link -> link.getAttributeFactoryMethodParameterName(methodName, methodAnnotations,
            parameterName, parameterAnnotations).stream()).findFirst();
  }

  public Optional<Boolean> isAttributeGetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return Chains.stream(this).flatMap(
            link -> link.isAttributeGetterFor(attributeName, methodName, methodAnnotations).stream())
        .findFirst();
  }

  public Optional<Boolean> isAttributeSetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return Chains.stream(this).flatMap(
            link -> link.isAttributeSetterFor(attributeName, methodName, methodAnnotations).stream())
        .findFirst();
  }

  public Optional<Boolean> isAttributeFieldFor(String attributeName, String fieldName,
      List<Annotation> fieldAnnotations) {
    return Chains.stream(this).flatMap(
            link -> link.isAttributeFieldFor(attributeName, fieldName, fieldAnnotations).stream())
        .findFirst();
  }

  public Optional<Boolean> isAttributeInputFor(String attributeName, String inputName,
      List<Annotation> inputAnnotations) {
    return Chains.stream(this).flatMap(
            link -> link.isAttributeInputFor(attributeName, inputName, inputAnnotations).stream())
        .findFirst();
  }
}
