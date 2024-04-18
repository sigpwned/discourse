package com.sigpwned.discourse.guava;

import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.guava.serialization.ByteSourceValueDeserializerFactory;

/**
 * An example {@link Module} for deserializing a handful of Guava types.
 */
public class GuavaModule extends Module {

  /**
   * <p>
   * Registers the Guava deserializers:
   * </p>
   *
   * <ul>
   *   <li>{@link ByteSourceValueDeserializerFactory}</li>
   * </ul>
   */
  @Override
  public void register(SerializationContext context) {
    context.addLast(ByteSourceValueDeserializerFactory.INSTANCE);
  }
}
