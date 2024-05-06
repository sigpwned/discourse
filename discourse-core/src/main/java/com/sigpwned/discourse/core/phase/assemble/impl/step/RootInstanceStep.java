package com.sigpwned.discourse.core.phase.assemble.impl.step;

import static java.util.stream.Collectors.toMap;

import com.sigpwned.discourse.core.phase.assemble.AssemblePhase;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class RootInstanceStep implements Consumer<Map<String, List<Object>>> {

  public static final String NAME = AssemblePhase.TARGET;

  private final Function<Map<String, Object>, Object> factory;

  public RootInstanceStep(Function<Map<String, Object>, Object> factory) {
    this.factory = Objects.requireNonNull(factory);
  }

  @Override
  public void accept(Map<String, List<Object>> transformedArgs) {
    Map<String, Object> factoryArgs = transformedArgs.entrySet().stream()
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

    transformedArgs.clear();

    Object root = getFactory().apply(factoryArgs);

    if (root == null) {
      // TODO better exception
      throw new IllegalArgumentException("Root instance factory produced null");
    }

    transformedArgs.put(NAME, List.of(root));
  }

  private Function<Map<String, Object>, Object> getFactory() {
    return factory;
  }
}
