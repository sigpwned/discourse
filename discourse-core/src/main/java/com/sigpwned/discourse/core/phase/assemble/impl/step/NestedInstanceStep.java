package com.sigpwned.discourse.core.phase.assemble.impl.step;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class NestedInstanceStep implements Consumer<Map<String, List<Object>>> {

  private final String name;
  private final Function<Map<String, Object>, Object> factory;

  public NestedInstanceStep(String name, Function<Map<String, Object>, Object> factory) {
    this.name = requireNonNull(name);
    this.factory = requireNonNull(factory);
  }

  @Override
  public void accept(Map<String, List<Object>> transformedArgs) {
    Map<String, List<Object>> rewrittenArguments = new HashMap<>();
    Iterator<Map.Entry<String, List<Object>>> iterator = transformedArgs.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, List<Object>> e = iterator.next();
      if (e.getKey().startsWith(getName() + ".")) {
        if (e.getValue() != null) {
          rewrittenArguments.put(e.getKey().substring(getName().length() + 1), e.getValue());
        }
        iterator.remove();
      }
    }

    Map<String, Object> factoryArgs = rewrittenArguments.entrySet().stream()
        .filter(e -> e.getValue() != null).<Map.Entry<String, Object>>mapMulti((e, downstream) -> {
          if (e.getValue().size() == 0) {
            // TODO better exception, or log?
            throw new IllegalArgumentException("Assembly phase produced no output values");
          } else if (e.getValue().size() > 1) {
            // TODO better exception, or log?
            throw new IllegalArgumentException("Assembly phase produced multiple output values");
          } else if (e.getValue().get(0) == null) {
            // This is fine. We just ignore it.
          } else {
            downstream.accept(Map.entry(e.getKey(), e.getValue().get(0)));
          }
        }).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

    Object nested = getFactory().apply(factoryArgs);

    if (nested != null) {
      transformedArgs.put(getName(), List.of(nested));
    }
  }

  public String getName() {
    return name;
  }

  private Function<Map<String, Object>, Object> getFactory() {
    return factory;
  }
}
