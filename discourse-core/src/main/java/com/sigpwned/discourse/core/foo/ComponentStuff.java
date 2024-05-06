package com.sigpwned.discourse.core.foo;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.chain.ConfigurableInstanceFactoryScannerChain;
import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.scanner.ConfigurableComponentScanner;
import com.sigpwned.discourse.core.foo.BuildableClassWalker.ConstructorSelector;
import com.sigpwned.discourse.core.foo.BuildableClassWalker.Visitor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ComponentStuff {

  public static interface ObjectFactory {

    public Object createObject(Map<String, Object> arguments);
  }

  public static <T> ObjectFactory mixin(Class<T> clazz, InvocationContext context) {
    final List<ConfigurableComponent> components = new ArrayList<>();

    ConfigurableInstanceFactoryScannerChain instanceFactoryScanner = context.get(
        InvocationContext.CONFIGURABLE_INSTANCE_FACTORY_PROVIDER_CHAIN_KEY).orElseThrow();
    ConfigurableComponentScanner componentScanner = context.get(
        InvocationContext.CONFIGURABLE_COMPONENT_SCANNER_CHAIN_KEY).orElseThrow();

    new BuildableClassWalker(new ConstructorSelector() {
      @Override
      public <U> Optional<Constructor<U>> selectConstructor(List<Constructor<U>> constructors) {
        // TODO Need to handle constructor and factory methods
        return Optional.empty();
      }
    }).walkClassAndAllSuperclasses(clazz, new BuildableClassWalker.Visitor<T>() {
      @Override
      public void beginClass(Class<? super T> clazz) {

      }

      @Override
      public void constructor(Constructor<? super T> constructor) {
        for(Parameter parameter : constructor.getParameters()) {
          components.addAll(componentScanner.scanForComponents(parameter.getType()));
        }
      }

      @Override
      public void field(Field field) {
        components.addAll(componentScanner.scanForComponents(field.getType()));
      }

      @Override
      public void method(Method method) {
        Visitor.super.method(method);
      }

      @Override
      public void endClass(Class<? super T> clazz) {
        Visitor.super.endClass(clazz);
      }
    });
  }

}
