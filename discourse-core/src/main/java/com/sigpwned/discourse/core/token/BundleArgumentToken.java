package com.sigpwned.discourse.core.token;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import com.sigpwned.discourse.core.ArgumentToken;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.util.Generated;

public class BundleArgumentToken extends ArgumentToken {
  private final List<String> shortNames;

  public BundleArgumentToken(String text, List<String> shortNames) {
    super(Type.BUNDLE, text);
    if (shortNames == null)
      throw new NullPointerException();
    if (shortNames.isEmpty())
      throw new IllegalArgumentException("empty bundle");
    if (!shortNames.stream().allMatch(ShortSwitchNameCoordinate.PATTERN.asMatchPredicate()))
      throw new IllegalArgumentException("invalid short names: " + shortNames.stream()
          .filter(Predicate.not(ShortSwitchNameCoordinate.PATTERN.asMatchPredicate()))
          .collect(joining(", ")));
    this.shortNames = unmodifiableList(shortNames);
  }

  /**
   * @return the shortNames
   */
  public List<String> getShortNames() {
    return shortNames;
  }

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(shortNames);
    return result;
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    BundleArgumentToken other = (BundleArgumentToken) obj;
    return Objects.equals(shortNames, other.shortNames);
  }
}
