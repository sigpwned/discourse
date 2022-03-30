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
package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.module.DefaultModule;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;

public class CommandBuilder {
  private final SerializationContext serializationContext;
  private final SinkContext sinkContext;

  public CommandBuilder() {
    this.serializationContext = new SerializationContext();
    this.sinkContext = new SinkContext(AssignValueSinkFactory.INSTANCE);
    register(new DefaultModule());
  }
  
  public CommandBuilder register(Module module) {
    module.register(getSerializationContext());
    module.register(getSinkContext());
    return this;
  }
  
  public <T> Command<T> build(Class<T> rawType) {
    if(rawType.getAnnotation(Configurable.class) == null)
      throw new NotConfigurableConfigurationException(rawType);
    return Command.scan(getSinkContext(), getSerializationContext(), rawType);
  }

  /**
   * @return the serializationContext
   */
  private SerializationContext getSerializationContext() {
    return serializationContext;
  }

  /**
   * @return the storageContext
   */
  private SinkContext getSinkContext() {
    return sinkContext;
  }

}
