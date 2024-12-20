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

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.DiscourseRequired;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;

/**
 * This is the simplest configuration for the given parameters.
 */
@Configurable
public class SimpleFizzBuzzConfiguration {
    @DiscourseRequired
    @PositionalParameter(position=0)
    public int count;
    
    @OptionParameter(shortName="f", longName="fizz")
    public String fizz = "fizz";
    
    @OptionParameter(shortName="b", longName="buzz")
    public String buzz = "buzz";
}
