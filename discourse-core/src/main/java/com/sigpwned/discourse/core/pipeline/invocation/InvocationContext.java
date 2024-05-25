package com.sigpwned.discourse.core.pipeline.invocation;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import java.util.Optional;

public interface InvocationContext {
  public static class Key<T> {
    public static <T> Key<T> of(Class<T> type) {
      return of(null, type);
    }

    public static <T> Key<T> of(String name, Class<T> type) {
      return new Key<>(name, type);
    }

    private final String name;
    private final Class<T> type;

    public Key(String name, Class<T> type) {
      this.name = name;
      this.type = requireNonNull(type);
    }

    public Optional<String> getName() {
      return Optional.ofNullable(name);
    }

    public Class<T> getType() {
      return type;
    }

    public boolean isAssignableFrom(Key<?> that) {
      return Objects.equals(this.name, that.name) && this.type.isAssignableFrom(that.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Key other = (Key) obj;
      return Objects.equals(name, other.name) && Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
      return "Key [name=" + name + ", type=" + type + "]";
    }
  }

  default <T> Optional<? extends T> get(Class<T> type) {
    return get(Key.of(type));
  }

  public <T> Optional<? extends T> get(Key<T> key);
}
