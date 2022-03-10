package com.sigpwned.discourse.examples.fizzbuzz.simple;

import com.sigpwned.discourse.core.util.Discourse;

public class SimpleFizzBuzz {
  public static void main(String[] args) {
    SimpleFizzBuzzConfiguration configuration =
        Discourse.configure(SimpleFizzBuzzConfiguration.class, args);
    if (configuration.count < 1)
      throw new IllegalArgumentException("count must be at least 1");
    for (int i = 1; i <= configuration.count; i++) {
      boolean mod3 = (i % 3) == 0;
      boolean mod5 = (i % 5) == 0;
      if (mod3 && mod5)
        System.out.println(configuration.fizz + " " + configuration.buzz);
      else if (mod3)
        System.out.println(configuration.fizz);
      else if (mod5)
        System.out.println(configuration.buzz);
      else
        System.out.println(i);
    }
  }
}
