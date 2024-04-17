package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ConfigurationParameters {

  private ConfigurationParameters() {
  }

  public static BiConsumer<ConfigurationParameter, Consumer<FlagConfigurationParameter>> mapMultiFlag() {
    return mapMulti(FlagConfigurationParameter.class);
  }

  public static <P extends ConfigurationParameter> BiConsumer<ConfigurationParameter, Consumer<P>> mapMulti(
      Class<P> clazz) {
    return (x, d) -> {
      if (clazz.isInstance(x)) {
        d.accept(clazz.cast(x));
      }
    };
  }
}
