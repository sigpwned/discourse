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
package com.sigpwned.discourse.core.invocation.context;

import static java.util.Collections.*;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * An implementation of {@link InvocationContext} that delegates to a chain of other invocation
 * contexts according to the Chain of Responsibility pattern.
 * </p>
 */
public class ChainInvocationContext implements InvocationContext {

  public static ChainInvocationContext of(InvocationContext first, InvocationContext... more) {
    List<InvocationContext> chain = new ArrayList<>(more.length + 1);
    chain.add(first);
    addAll(chain, more);
    return of(chain);
  }

  public static ChainInvocationContext of(List<InvocationContext> chain) {
    return new ChainInvocationContext(chain);
  }

  private final List<InvocationContext> chain;

  /**
   * Creates a new invocation context that delegates to the given chain of invocation contexts.
   *
   * @param chain The chain of invocation contexts. Must not be empty.
   */
  public ChainInvocationContext(List<InvocationContext> chain) {
    this.chain = unmodifiableList(chain);
    if (getChain().isEmpty()) {
      throw new IllegalArgumentException("chain must not be empty");
    }
  }

  private List<InvocationContext> getChain() {
    return chain;
  }

  /**
   * Registers a module with the first invocation context in the chain.
   *
   * @param module The module to register. Must not be null.
   */
  @Override
  public void register(Module module) {
    getChain().get(0).register(module);
  }

  /**
   * Sets a value in the first invocation context in the chain.
   *
   * @param key   The key to set. Must not be null.
   * @param value The value to set. Must not be null.
   */
  @Override
  public <T> void set(Key<T> key, T value) {
    getChain().get(0).set(key, value);
  }

  /**
   * Gets a value from the chain of invocation contexts. The chain is traversed in order until a
   * value is found. If no value is found, an empty optional is returned.
   *
   * @param key The key to get. Must not be null.
   * @return The value, if found.
   */
  @Override
  public <T> Optional<T> get(Key<T> key) {
    Optional<T> result = Optional.empty();
    for (InvocationContext link : getChain()) {
      result = link.get(key);
      if (result.isPresent()) {
        return result;
      }
    }
    return result;
  }
}
