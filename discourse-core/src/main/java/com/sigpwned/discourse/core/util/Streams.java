package com.sigpwned.discourse.core.util;

import static java.util.stream.Collectors.groupingBy;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Streams {

  private Streams() {
  }

  /**
   * Returns a stream of entries representing the occurrences of each element in the input stream.
   *
   * @param stream the input stream
   * @param <T>    the type of the elements in the input stream
   * @return a stream of entries representing the occurrences of each element in the input stream
   */
  public static <T> Stream<Map.Entry<T, Long>> occurrences(Stream<T> stream) {
    return stream.collect(groupingBy(Function.identity(), Collectors.counting())).entrySet()
        .stream();
  }

  /**
   * Returns a stream of elements that occur more than once in the input stream.
   *
   * @param stream the input stream
   * @param <T>    the type of the elements in the input stream
   * @return a stream of elements that occur more than once in the input stream
   */
  public static <T> Stream<T> duplicates(Stream<T> stream) {
    return occurrences(stream).filter(e -> e.getValue() > 1).map(Map.Entry::getKey);
  }
}
