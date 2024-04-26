package com.sigpwned.discourse.core.configurable.component.scanner;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.GetterConfigurableComponent;
import com.sigpwned.discourse.core.util.ClassWalkers;
import com.sigpwned.discourse.core.util.Streams;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * A {@link ConfigurableComponentScanner} that scans for instance getter {@link Method methods}. An
 * instance getter method is a method that takes no arguments, returns a value, and is not static.
 * Note that this scanner does not care about the name of the method or its visibility.
 */
public class GetterConfigurableComponentScanner implements ConfigurableComponentScanner {

  public static final GetterConfigurableComponentScanner INSTANCE = new GetterConfigurableComponentScanner();

  @Override
  public List<ConfigurableComponent> scanForComponents(Class<?> rawType,
      InvocationContext context) {
    return ClassWalkers.streamClassAndSuperclasses(rawType)
        .mapMulti(Streams.filterAndCast(Method.class)).filter(
            method -> method.getParameterCount() == 0 && !void.class.equals(method.getReturnType())
                && !Modifier.isStatic(method.getModifiers()))
        .<ConfigurableComponent>map(GetterConfigurableComponent::new).toList();
  }
}
