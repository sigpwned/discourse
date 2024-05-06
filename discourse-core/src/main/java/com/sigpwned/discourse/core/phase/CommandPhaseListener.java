package com.sigpwned.discourse.core.phase;

import com.sigpwned.discourse.core.configurable.ConfigurableClass;
import com.sigpwned.discourse.core.configurable.ConfigurableClassScanner;
import com.sigpwned.discourse.core.configurable.ConfigurableClassWalker;
import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.model.command.Discriminator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

public class CommandPhaseListener<T> implements PhaseListener {

  private final ConfigurableClassScanner scanner;
  private final Class<T> rootType;
  private ConfigurableClass<? extends T> configurableType;

  @Override
  public void beforeResolve(Map<List<String>, Object> choices, List<String> args) {
    new ConfigurableClassWalker<T>(rootType).walk(rootType,
        new ConfigurableClassWalker.Visitor<T>() {
          private final Stack<String> discriminators = new Stack<>();

          @Override
          public <M extends T> void enterMultiCommandClass(Discriminator discriminator, String name,
              String description, String version, Class<M> clazz) {
            if (discriminator != null) {
              discriminators.push(discriminator.toString());
            }
          }

          @Override
          public <M extends T> void leaveMultiCommandClass(Discriminator discriminator, String name,
              String description, String version, Class<M> clazz) {
            if (discriminator != null) {
              discriminators.pop();
            }
          }

          @Override
          public <S extends T> void visitSingleCommandClass(Discriminator discriminator,
              String name, String description, String version, Class<S> clazz) {
            if (discriminator != null) {
              discriminators.push(discriminator.toString());
              choices.put(List.copyOf(discriminators), clazz);
              discriminators.pop();
            } else {
              choices.put(List.of(), clazz);
            }
          }
        });
  }

  @Override
  public void afterResolve(Map<List<String>, Object> choices, List<String> args,
      Map.Entry<List<String>, Object> selection) {
    if (!(selection.getValue() instanceof Class<?> clazz)) {
      // This means that some other listener is messing with the selection
      return;
    }

    Class<? extends T> selectedType;
    try {
      selectedType = clazz.asSubclass(rootType);
    } catch (ClassCastException e) {
      // This means that some other listener is messing with the selection
      return;
    }

    configurableType = scanner.scan(selectedType);
  }

  @Override
  public void beforeParse(Map<String, String> vocabulary, List<String> args) {
    if (configurableType == null) {
      // This means that some other listener is doing our configuration
      return;
    }

    for(ConfigurableComponent )
  }

  @Override
  public void afterParse(Map<String, String> vocabulary, List<String> args,
      List<Entry<Object, String>> parsedArgs) {
    if (configurableType == null) {
      // This means that some other listener is doing our configuration
      return;
    }
  }

  @Override
  public void beforeCollect(Map<Object, String> propertyNames,
      List<Entry<Object, String>> parsedArgs) {
    if (configurableType == null) {
      // This means that some other listener is doing our configuration
      return;
    }
  }

  @Override
  public void afterCollect(Map<Object, String> propertyNames,
      List<Entry<Object, String>> parsedArgs, Map<String, List<String>> collectedArgs) {
    if (configurableType == null) {
      // This means that some other listener is doing our configuration
      return;
    }
  }

  @Override
  public void beforeTransform(Map<String, Function<String, Object>> transformations,
      Map<String, List<String>> collectedArgs) {
    if (configurableType == null) {
      // This means that some other listener is doing our configuration
      return;
    }
  }

  @Override
  public void afterTransform(Map<String, Function<String, Object>> transformations,
      Map<String, List<String>> collectedArgs, Map<String, List<Object>> transformedArgs) {
    if (configurableType == null) {
      // This means that some other listener is doing our configuration
      return;
    }
  }

  @Override
  public void beforeAssemble(List<Consumer<Map<String, List<Object>>>> steps,
      Map<String, List<Object>> transformedArgs) {
    if (configurableType == null) {
      // This means that some other listener is doing our configuration
      return;
    }
  }

  @Override
  public void afterAssemble(List<Consumer<Map<String, List<Object>>>> steps,
      Map<String, List<Object>> transformedArgs, Object configuration) {
    if (configurableType == null) {
      // This means that some other listener is doing our configuration
      return;
    }
  }
}
