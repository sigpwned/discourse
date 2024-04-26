/*-
 * =================================LICENSE_START==================================
 * discourse-core
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
package com.sigpwned.discourse.core.exception.bean;

import com.sigpwned.discourse.core.exception.BeanException;
import com.sigpwned.discourse.core.value.sink.ValueSink;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.lang.reflect.InvocationTargetException;

/**
 * Thrown when an assignment to a property fails. This is usually thrown in response to an
 * {@link InvocationTargetException} that occurs when a setter method is called.
 *
 * @see ValueSink#put(Object, Object)
 */
public class AssignmentFailureBeanException extends BeanException {

  private final String propertyName;

  public AssignmentFailureBeanException(SingleCommand<?> command, String propertyName,
      Exception cause) {
    super(command, "Failed to assign to property %s".formatted(propertyName), cause);
    this.propertyName = propertyName;
  }

  public String getPropertyName() {
    return propertyName;
  }
}
