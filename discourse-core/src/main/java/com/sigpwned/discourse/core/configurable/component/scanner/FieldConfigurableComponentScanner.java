package com.sigpwned.discourse.core.configurable.component.scanner;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.FieldConfigurableComponent;
import com.sigpwned.discourse.core.util.ClassWalkers;
import com.sigpwned.discourse.core.util.Streams;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * A {@link ConfigurableComponentScanner} that scans for instance (i.e., non-static)
 * {@link Field fields}. Note that this scanner does not care about the visibility of this field.
 */
public class FieldConfigurableComponentScanner implements ConfigurableComponentScanner {

  public static final FieldConfigurableComponentScanner INSTANCE = new FieldConfigurableComponentScanner();

  @Override
  public List<ConfigurableComponent> scanForComponents(Class<?> rawType,
      InvocationContext context) {
    return ClassWalkers.streamClassAndSuperclasses(rawType)
        .mapMulti(Streams.filterAndCast(Field.class))
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .<ConfigurableComponent>map(FieldConfigurableComponent::new).toList();
  }
}
