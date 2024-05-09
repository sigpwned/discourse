package com.sigpwned.discourse.core.util;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class MoreCollectors {

  private MoreCollectors() {
  }

  public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> entriesToMap() {
    return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
  }

  private static IllegalStateException duplicateKeyException(Object k, Object u, Object v) {
    return new IllegalStateException(
        String.format("Duplicate key %s (attempted merging values %s and %s)", k, u, v));
  }
}
