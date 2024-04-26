package com.sigpwned.discourse.core.configurable.component.scanner;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.SetterConfigurableComponent;
import com.sigpwned.discourse.core.util.ClassWalkers;
import com.sigpwned.discourse.core.util.Streams;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * A {@link ConfigurableComponentScanner} that scans for instance setter {@link Method methods}. An
 * instance setter method is a method that takes exactly one argument, returns void, and is not
 * static. Note that this scanner does not care about the name of the method or its visibility.
 */
public class SetterConfigurableComponentScanner implements ConfigurableComponentScanner {

  public static SetterConfigurableComponentScanner INSTANCE = new SetterConfigurableComponentScanner();

  @Override
  public List<ConfigurableComponent> scanForComponents(Class<?> rawType,
      InvocationContext context) {
    return ClassWalkers.streamClassAndSuperclasses(rawType)
        .mapMulti(Streams.filterAndCast(Method.class)).filter(
            method -> method.getParameterCount() == 1 && void.class.equals(method.getReturnType())
                && !Modifier.isStatic(method.getModifiers()))
        .<ConfigurableComponent>map(SetterConfigurableComponent::new).toList();
  }
}
