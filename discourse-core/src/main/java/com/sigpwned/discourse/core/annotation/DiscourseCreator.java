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
package com.sigpwned.discourse.core.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.sigpwned.discourse.core.command.Command;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Indicates that a constructor or factory method is the designated creator for {@link Command}
 * object instances. When a class does not have a default constructor or when a class has multiple
 * constructors or factory methods, this annotation must be used to indicate which constructor or
 * factory method should be used to create instances of the class.
 * </p>
 *
 * <p>
 * A class may have at most one constructor or factory method annotated with this annotation.
 * Otherwise, it would be ambiguous which constructor or factory method to use to create instances.
 * </p>
 *
 * <p>
 * For those familiar with the Jackson JSON processing library, this annotation is similar to
 * Jackson's {@code @JsonCreator} annotation.
 * </p>
 */
@Retention(RUNTIME)
@Target({CONSTRUCTOR, METHOD})
public @interface DiscourseCreator {

}
