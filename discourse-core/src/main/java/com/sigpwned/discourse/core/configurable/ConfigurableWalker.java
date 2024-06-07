package com.sigpwned.discourse.core.configurable;

import java.util.Objects;
import java.util.Optional;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.Subcommand;

public class ConfigurableWalker {
  public static interface Listener<T> {
    public void enterRootConfigurable(Class<T> clazz, String name, String version);

    public void enterSubConfigurable(String discriminator, Class<? extends T> clazz);

    public void leaveSubConfigurable();

    public void leaveRootConfigurable();
  }

  public <T> void walk(Class<T> clazz, Listener<T> listener) {
    Configurable configurable = clazz.getAnnotation(Configurable.class);
    if (configurable == null) {
      // TODO better exception
      throw new IllegalArgumentException("class " + clazz + " is not configurable");
    }

    String name = fromAnnotationString(configurable.name()).orElse(null);
    String version = fromAnnotationString(configurable.version()).orElse(null);

    listener.enterRootConfigurable(clazz, name, version);
    if (configurable.subcommands().length > 0) {
      subwalk(clazz, configurable.subcommands(), listener);
    }
    listener.leaveRootConfigurable();
  }


  protected <T, U extends T> void subwalk(Class<U> superclazz, Subcommand[] subcommands,
      Listener<T> listener) {
    for (Subcommand subcommand : subcommands) {
      String expectedDiscriminator =
          fromAnnotationString(subcommand.discriminator()).orElseThrow(() -> {
            // TODO better exception
            return new IllegalArgumentException("subcommand must have discriminator");
          });

      @SuppressWarnings({"unchecked", "rawtypes"})
      Class<? extends U> subclazz = (Class) subcommand.configurable();
      if (!superclazz.isAssignableFrom(subclazz)) {
        // TODO better exception
        throw new IllegalArgumentException(
            "subclass " + subclazz + " is not a subclass of " + superclazz);
      }

      Configurable configurable = subclazz.getAnnotation(Configurable.class);
      if (configurable == null) {
        // TODO better exception
        throw new IllegalArgumentException("subclass " + subclazz + " is not configurable");
      }

      String discriminator = fromAnnotationString(configurable.discriminator()).orElse(null);
      if (!Objects.equals(discriminator, expectedDiscriminator)) {
        // TODO better exception
        throw new IllegalArgumentException("subclass " + subclazz + " has discriminator "
            + discriminator + " but expected " + expectedDiscriminator);
      }

      listener.enterSubConfigurable(discriminator, subclazz);
      if (configurable.subcommands().length > 0) {
        subwalk(subclazz, configurable.subcommands(), listener);
      }
      listener.leaveSubConfigurable();
    }
  }

  protected static Optional<String> fromAnnotationString(String annotationString) {
    return annotationString.isEmpty() ? Optional.empty() : Optional.of(annotationString);
  }
}
