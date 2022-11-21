/*-
 * =================================LICENSE_START==================================
 * discourse-examples
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
package com.sigpwned.discourse.examples.fizzbuzz.simple;

import com.sigpwned.discourse.core.util.Discourse;

public class SimpleFizzBuzz {
  public static void main(String[] args) {
    main(Discourse.configuration(SimpleFizzBuzzConfiguration.class, args));
  }

  public static void main(SimpleFizzBuzzConfiguration configuration) {
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
