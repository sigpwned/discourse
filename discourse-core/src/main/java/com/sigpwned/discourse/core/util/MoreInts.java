package com.sigpwned.discourse.core.util;

public final class MoreInts {
  private MoreInts() {}

  public static int sum(int[] xs) {
    int result = 0;
    for (int i = 0; i < xs.length; i++)
      result = result + xs[i];
    return result;
  }
}
