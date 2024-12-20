/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core.util;

import static java.util.stream.Collectors.groupingBy;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Streams {

  private Streams() {}

  /**
   * Returns a stream that concatenates the given stream.
   *
   * @param only the stream to concatenate
   * @param <T> the type of the elements in the stream
   * @return a stream that concatenates the input stream
   */
  public static <T> Stream<T> concat(Stream<T> only) {
    return only;
  }

  /**
   * Returns a stream that concatenates the given streams.
   *
   * @param first the first stream
   * @param second the second stream
   * @param <T> the type of the elements in the streams
   * @return a stream that concatenates the input streams
   */
  public static <T> Stream<T> concat(Stream<? extends T> first, Stream<? extends T> second) {
    return Stream.concat(first, second);
  }

  /**
   * Returns a stream that concatenates the given streams.
   *
   * @param first the first stream
   * @param second the second stream
   * @param more additional streams
   * @param <T> the type of the elements in the streams
   * @return a stream that concatenates the input streams
   */
  public static <T> Stream<T> concat(Stream<? extends T> first, Stream<? extends T> second,
      Stream<? extends T>... more) {
    Stream<T> result = Stream.concat(first, second);
    for (Stream<? extends T> stream : more) {
      result = Stream.concat(result, stream);
    }
    return result;
  }

  /**
   * Returns a stream of entries representing the occurrences of each element in the input stream.
   *
   * @param stream the input stream
   * @param <T> the type of the elements in the input stream
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
   * @param <T> the type of the elements in the input stream
   * @return a stream of elements that occur more than once in the input stream
   */
  @Deprecated
  public static <T> Stream<T> duplicates(Stream<T> stream) {
    return occurrences(stream).filter(e -> e.getValue() > 1).map(Map.Entry::getKey);
  }

  /**
   * Returns a {@link Stream#mapMulti(BiConsumer)} operator that filters elements of the stream to
   * the specified class and casts the elements to that class in one step.
   *
   * @param clazz the class to filter and cast elements to
   * @param <U> the type to filter and cast elements to
   * @return a {@link Stream#mapMulti(BiConsumer)} operator that filters elements of the stream to
   *         the specified class and casts the elements to that class in one step
   */
  public static <T, U extends T> BiConsumer<T, Consumer<U>> filterAndCast(Class<U> clazz) {
    return (x, d) -> {
      if (clazz.isInstance(x)) {
        d.accept(clazz.cast(x));
      }
    };
  }
}
