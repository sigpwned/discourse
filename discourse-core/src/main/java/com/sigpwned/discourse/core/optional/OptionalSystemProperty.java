package com.sigpwned.discourse.core.optional;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An optional system property. It is intended to be used in a similar way to {@link Optional}, but
 * for system properties only. It retains the name of the system property, which can be useful for
 * error messages and provides a more fluent approach to application configuration.
 */
public class OptionalSystemProperty<T> {

  public static OptionalSystemProperty<String> getProperty(String name) {
    return new OptionalSystemProperty<String>(name, System.getProperty(name));
  }

  private final String name;
  private final T value;

  private OptionalSystemProperty(String name, T value) {
    if (name == null) {
      throw new NullPointerException();
    }
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public boolean isPresent() {
    return !isEmpty();
  }

  public boolean isEmpty() {
    return value == null;
  }

  public <X> OptionalSystemProperty<X> map(Function<T, X> f) {
    return new OptionalSystemProperty<>(getName(), isPresent() ? f.apply(value) : null);
  }

  public <X> OptionalSystemProperty<X> flatMap(Function<T, Optional<X>> f) {
    return new OptionalSystemProperty<>(getName(),
        isPresent() ? f.apply(value).orElse(null) : null);
  }

  public Stream<T> stream() {
    return isPresent() ? Stream.of(value) : Stream.empty();
  }

  public OptionalSystemProperty<T> filter(Predicate<T> test) {
    return new OptionalSystemProperty<>(getName(), isPresent() && test.test(value) ? value : null);
  }

  public void ifPresent(BiConsumer<String, ? super T> action) {
    ifPresentOrElse(action, name -> {
    });
  }

  public void ifPresentOrElse(BiConsumer<String, ? super T> action, Consumer<String> emptyAction) {
    if (isPresent()) {
      action.accept(getName(), value);
    } else {
      emptyAction.accept(getName());
    }
  }

  public OptionalSystemProperty<T> or(Supplier<? extends Optional<? extends T>> supplier) {
    if (isPresent()) {
      return this;
    } else {
      @SuppressWarnings("unchecked") Optional<T> r = (Optional<T>) supplier.get();
      return new OptionalSystemProperty<>(getName(), Objects.requireNonNull(r).orElse(null));
    }
  }

  public T orElse(T defaultValue) {
    return isPresent() ? value : defaultValue;
  }

  public T orElseGet(Supplier<T> defaultValue) {
    return isPresent() ? value : defaultValue.get();
  }

  public <E extends Exception> T orElseThrow(Function<String, E> supplier) throws E {
    if (isEmpty()) {
      throw supplier.apply(getName());
    }
    return value;
  }

  public <E extends Exception> T orElseThrow() throws E {
    return orElseThrow(name -> new NoSuchElementException("No value for system property " + name));
  }

  public T get() {
    return orElseThrow();
  }
}