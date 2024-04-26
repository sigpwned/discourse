package com.sigpwned.discourse.core.configurable;

import com.sigpwned.discourse.core.ConfigurableClass;
import com.sigpwned.discourse.core.ConfigurableInstanceFactory;
import com.sigpwned.discourse.core.ConfigurableInstanceFactoryProviderChain;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.FieldConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.GetterConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.SetterConfigurableComponent;
import com.sigpwned.discourse.core.util.ClassWalkers;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class DefaultConfigurableClassScanner {

  public <T> ConfigurableClass<T> scan(Class<T> clazz, InvocationContext context) {
    final ConfigurableInstanceFactoryProviderChain chain = context.get(
        InvocationContext.CONFIGURABLE_INSTANCE_FACTORY_PROVIDER_CHAIN_KEY).orElseThrow();

    ConfigurableInstanceFactory<T> instanceFactory = chain.getConfigurationInstanceFactory(clazz)
        .orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException(
              "No configuration instance factory for class " + clazz);
        });

    List<ConfigurableComponent> instanceComponents = ClassWalkers.streamClassAndSuperclasses(clazz)
        .<ConfigurableComponent>mapMulti((ao, downstream) -> {
          if (ao instanceof Field field) {
            downstream.accept(new FieldConfigurableComponent(field));
          } else if (ao instanceof Method method) {
            if (hasGetterSignature(method)) {
              downstream.accept(new GetterConfigurableComponent(method));
            }
            if (hasSetterSignature(method)) {
              downstream.accept(new SetterConfigurableComponent(method));
            }
          }
        }).toList();

    return new ConfigurableClass<>(clazz, instanceFactory, instanceComponents);
  }

  private static boolean hasGetterSignature(Method method) {
    return method.getParameterCount() == 0 && method.getReturnType() != void.class;
  }

  private static boolean hasSetterSignature(Method method) {
    return method.getParameterCount() == 1 && method.getReturnType() == void.class;
  }
}
