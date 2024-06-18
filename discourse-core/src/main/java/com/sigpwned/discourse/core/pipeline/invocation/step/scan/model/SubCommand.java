package com.sigpwned.discourse.core.pipeline.invocation.step.scan.model;

import static java.util.Objects.requireNonNull;
import java.util.Map;
import com.sigpwned.discourse.core.command.Discriminator;

public class SubCommand<T> {
  private final Class<T> clazz;
  private final Discriminator discriminator;
  private final String description;
  private final CommandBody body;
  private final Map<Discriminator, SubCommand<? extends T>> subs;

  public SubCommand(Class<T> clazz, Discriminator discriminator, String description,
      CommandBody body, Map<Discriminator, SubCommand<? extends T>> subs) {
    this.clazz = requireNonNull(clazz);
    this.discriminator = requireNonNull(discriminator);
    this.description = description;
    this.body = requireNonNull(body);
    this.subs = requireNonNull(subs);
  }

  /**
   * @return the clazz
   */
  public Class<T> getClazz() {
    return clazz;
  }

  /**
   * @return the discriminator
   */
  public Discriminator getDiscriminator() {
    return discriminator;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the body
   */
  public CommandBody getBody() {
    return body;
  }

  /**
   * @return the subs
   */
  public Map<Discriminator, SubCommand<? extends T>> getSubs() {
    return subs;
  }
}
