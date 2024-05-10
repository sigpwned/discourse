package com.sigpwned.discourse.core.invocation.phase.parse;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.invocation.phase.parse.group.GroupPhase;
import com.sigpwned.discourse.core.invocation.phase.parse.map.MapPhase;
import com.sigpwned.discourse.core.invocation.phase.parse.parse.ParsePhase;
import com.sigpwned.discourse.core.invocation.phase.parse.reduce.ReducePhase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ParsePipeline {

  private final ParsePhase parsePhase;
  private final GroupPhase groupPhase;
  private final MapPhase mapPhase;
  private final ReducePhase reducePhase;
  private final ParsePipelineListenerChain listener;

  public ParsePipeline(ParsePhase parsePhase, GroupPhase groupPhase, MapPhase mapPhase,
      ReducePhase reducePhase) {
    this.parsePhase = requireNonNull(parsePhase);
    this.groupPhase = requireNonNull(groupPhase);
    this.mapPhase = requireNonNull(mapPhase);
    this.reducePhase = requireNonNull(reducePhase);
    this.listener = new ParsePipelineListenerChain();
  }

  public Map<String, Object> execute(List<String> commandArgs) {
    // Parse Phase. Convert command-line arguments into a structured form. The result is a list of
    // pairs, where each pair is a mapping from a coordinate to a string. The coordinate is a syntax
    // identifier that is used to identify the position of the argument in the command line (e.g.,
    // "-x" for an argument to the short option "x", "--xray" for an argument to the long option
    // "xray", or 0 for the first positional argument). The order reflects the order of the syntax
    // elements in the command line.
    List<Map.Entry<Object, String>> parsedArgs = parsePhase(commandArgs);

    // Group Phase. Convert the coordinates in the parsed from into parameter names, and then group
    // the results into lists by parameter name. The order of the keys in the map is arbitrary. The
    // order of the values in the values of the map is preserved from the parsed form.
    Map<String, List<String>> groupedArgs = groupPhase(parsedArgs);

    // Map phase. Convert the strings in the grouped form into a structured form. The result is a map
    // from parameter names to lists of objects. The order of the keys in the map is arbitrary. The
    // order of the values in the values of the map is preserved from the grouped form.
    Map<String, List<Object>> mappedArgs = mapPhase(groupedArgs);

    // Reduce phase. Convert the lists in the mapped form into a structured form. The result is a map
    // from parameter names to logical parameter values. The order of the keys in the map is
    // arbitrary.
    Map<String, Object> reducedArgs = reducePhase(mappedArgs);

    return reducedArgs;
  }

  protected List<Map.Entry<Object, String>> parsePhase(List<String> commandArgs) {
    Map<String, String> vocabulary = new HashMap<>();

    getListener().beforeParse(vocabulary, commandArgs);

    List<Map.Entry<Object, String>> parsedArgs = getParsePhase().parse(vocabulary, commandArgs);

    getListener().afterParse(unmodifiableMap(vocabulary), unmodifiableList(commandArgs),
        parsedArgs);

    return parsedArgs;
  }

  protected Map<String, List<String>> groupPhase(List<Map.Entry<Object, String>> parsedArgs) {
    Map<Object, String> names = new HashMap<>();

    getListener().beforeGroup(names, parsedArgs);

    Map<String, List<String>> groupedArgs = getGroupPhase().group(parsedArgs, names);

    getListener().afterGroup(names, parsedArgs, groupedArgs);

    return groupedArgs;
  }

  protected Map<String, List<Object>> mapPhase(Map<String, List<String>> groupedArgs) {
    Map<String, Function<String, Object>> mappers = new HashMap<>();

    getListener().beforeMap(mappers, groupedArgs);

    Map<String, List<Object>> mappedArgs = getMapPhase().map(groupedArgs, mappers);

    getListener().afterMap(mappers, groupedArgs, mappedArgs);

    return mappedArgs;
  }

  protected Map<String, Object> reducePhase(Map<String, List<Object>> mappedArgs) {
    Map<String, Function<List<Object>, Object>> reducers = new HashMap<>();

    getListener().beforeReduce(reducers, mappedArgs);

    Map<String, Object> configuration = getReducePhase().reduce(mappedArgs, reducers);

    getListener().afterReduce(reducers, mappedArgs, configuration);

    return configuration;
  }

  protected ParsePhase getParsePhase() {
    return parsePhase;
  }

  protected GroupPhase getGroupPhase() {
    return groupPhase;
  }

  protected MapPhase getMapPhase() {
    return mapPhase;
  }

  protected ReducePhase getReducePhase() {
    return reducePhase;
  }

  protected ParsePipelineListenerChain getListener() {
    return listener;
  }
}
