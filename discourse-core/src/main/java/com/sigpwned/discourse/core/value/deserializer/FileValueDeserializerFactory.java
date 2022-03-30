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
package com.sigpwned.discourse.core.value.deserializer;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;

public class FileValueDeserializerFactory implements ValueDeserializerFactory<File> {
  public static final FileValueDeserializerFactory INSTANCE=new FileValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return genericType.equals(File.class);
  }

  @Override
  public ValueDeserializer<File> getDeserializer(Type genericType, List<Annotation> annotations) {
    return File::new;
  }
}
