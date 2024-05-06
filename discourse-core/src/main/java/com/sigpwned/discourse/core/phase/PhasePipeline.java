package com.sigpwned.discourse.core.phase;

import com.sigpwned.discourse.core.phase.assemble.impl.DefaultAssemblePhase;
import com.sigpwned.discourse.core.phase.collect.impl.DefaultCollectPhase;
import com.sigpwned.discourse.core.phase.parse.impl.DefaultParsePhase;
import com.sigpwned.discourse.core.phase.resolve.impl.DefaultResolvePhase;
import com.sigpwned.discourse.core.phase.transform.impl.DefaultTransformPhase;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class PhasePipeline {

  public Object go(List<String> args) {
    // Resolve Phase. Does this belong in the phase pipeline?
    Map.Entry<List<String>, Object> choice = resolvePhase(args);
    List<String> prefix = choice.getKey();
    List<String> remainingArgs = args.subList(prefix.size(), args.size());

    // Parse Phase. Convert command-line arguments into a structured form. The result is a list of
    // pairs, where each pair is a mapping from a coordinate to a string. The coordinate is a syntax
    // identifier that is used to identify the position of the argument in the command line (e.g.,
    // "-x" for an argument to the short option "x", "--xray" for an argument to the long option
    // "xray", or 0 for the first positional argument). The order reflects the order of the syntax
    // elements in the command line.
    List<Map.Entry<Object, String>> parsedArgs = parsePhase(remainingArgs);

    // Correlate Phase. Convert the coordinates in the parsed form into parameter names. The result
    // is a list of pairs, where each pair is a mapping from a parameter name to a string. The
    // parameter name is the name of the logical parameter that the argument corresponds to (e.g.,
    // "alpha", "bravo", etc.) The order is preserved from the parsed form.
    List<Map.Entry<String, String>> correlatedArgs = correlatePhase(parsedArgs);

    // Collect Phase. Convert the list in the correlated form into a map. The result is a map from
    // parameter names to lists of strings. The order of the keys in the map is arbitrary. The order
    // of the values in the values of the map is preserved from the correlated form.
    Map<String, List<String>> groupedArgs = groupPhase(correlatedArgs);

    // Map phase. Convert the strings in the grouped form into a structured form. The result is a map
    // from parameter names to lists of objects. The order of the keys in the map is arbitrary. The
    // order of the values in the values of the map is preserved from the grouped form.
    Map<String, List<Object>> mappedArgs = mapPhase(groupedArgs);

    // Reduce phase. Convert the lists in the mapped form into a structured form. The result is a map
    // from parameter names to logical parameter values. The order of the keys in the map is
    // arbitrary.
    Map<String, Object> reducedArgs = reducePhase(mappedArgs);

    // Assemble Phase. Convert the map in the reduced form into a structured form. The result is a
    // single object that represents the configuration. The order of the keys in the map is
    // arbitrary.
    Object configuration = assemblePhase(reducedArgs);

    return configuration;
  }

  protected Map.Entry<List<String>, Object> resolvePhase(List<String> originalArgs) {
    Map<List<String>, Object> choices = new HashMap<>();

    // beforeResolve(choices, args)

    Object choiceValue = new DefaultResolvePhase().resolve(choices, originalArgs)
        .orElseThrow(() -> {
          // TODO better exception. internal.
          return new AssertionError("no prefix found for args");
        });

    List<String> choiceKey = choices.entrySet().stream().filter(e -> e.getValue() == choiceValue)
        .map(Map.Entry::getKey).findFirst().orElseThrow(() -> {
          // TODO better exception?
          // This can only happen if ResolvePhase does not honor its contract.
          return new RuntimeException("naughty ResolvePhase");
        });

    // afterResolve(prefixes, args, prefix, selected)

    return new SimpleEntry<>(choiceKey, choiceValue);
  }

  protected List<Map.Entry<Object, String>> parsePhase(List<String> remainingArgs) {
    Map<String, String> vocabulary = new HashMap<>();

    // beforeParse(vocabulary, args)

    List<Map.Entry<Object, String>> parsedArgs = new DefaultParsePhase().parse(vocabulary,
        remainingArgs);

    // afterParse(vocabulary, args, parsedArgs)

    return parsedArgs;
  }

  protected Map<String, List<String>> groupPhase(List<Map.Entry<Object, String>> parsedArgs) {
    Map<Object, String> names = new HashMap<>();
    // beforeCorrelate(names, parsedArgs)
    Map<String, List<String>> collectedArgs = new DefaultCollectPhase().collect(names, parsedArgs);
    // afterCorrelate(names, parsedArgs, collectedArgs)
    return collectedArgs;
  }

  protected Map<String, List<Object>> transformPhase(Map<String, List<String>> collectedArgs) {
    Map<String, Function<String, Object>> transformations = new HashMap<>();

    // beforeTransform(transformations)

    Map<String, List<Object>> transformedArgs = new DefaultTransformPhase().transform(
        transformations, collectedArgs);

    // afterTransform(transformedArgs)

    return transformedArgs;
  }

  protected Object assemblePhase(Map<String, List<Object>> transformedArgs) {
    List<Consumer<Map<String, List<Object>>>> steps = new ArrayList<>();

    // beforeAssemble(assembler, deserializedArgs)

    Object configuration = new DefaultAssemblePhase().assemble(steps, transformedArgs);

    // afterAssemble(assembler, deserializedArgs, configuration)

    return configuration;
  }

}
