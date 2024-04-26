package com.sigpwned.discourse.core.util.collectors;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public final class Only<T> {

  public static <T> Collector<T, ?, Only<T>> toOnly() {
    return Collector.of(Only::new, Only<T>::add, (a, b) -> {
      a.merge(b);
      return a;
    });
  }

  private T firstValue;
  private int size;

  public Only() {
  }

  private void add(T value) {
    if (size == 0) {
      this.firstValue = value;
    }
    size = size + 1;
  }

  private void merge(Only<T> that) {
    if (this.size == 0) {
      this.firstValue = that.firstValue;
    }
    this.size = this.size + that.size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean isPresent() {
    return size == 1;
  }

  public boolean isOverflowed() {
    return size > 1;
  }

  public T get() {
    return orElseThrow(NoSuchElementException::new, this::newTooManyElementsException);
  }

  public Optional<T> toOptional() {
    return toOptional(this::newTooManyElementsException);
  }

  public Optional<T> toOptional(Supplier<? extends RuntimeException> multiExceptionSupplier) {
    return switch (size) {
      case 0 -> Optional.empty();
      case 1 -> Optional.ofNullable(firstValue);
      default -> throw multiExceptionSupplier.get();
    };
  }

  public T orElse(T other) {
    return orElse(other, this::newTooManyElementsException);
  }

  public T orElse(T other, Supplier<? extends RuntimeException> multiExceptionSupplier) {
    return switch (size) {
      case 0 -> other;
      case 1 -> firstValue;
      default -> throw multiExceptionSupplier.get();
    };
  }

  public T orElseGet(Supplier<? extends T> other) {
    return orElseGet(other, this::newTooManyElementsException);
  }

  public T orElseGet(Supplier<? extends T> other,
      Supplier<? extends RuntimeException> multiExceptionSupplier) {
    return switch (size) {
      case 0 -> other.get();
      case 1 -> firstValue;
      default -> throw multiExceptionSupplier.get();
    };
  }

  public T orElseThrow() {
    return orElseThrow(NoSuchElementException::new, this::newTooManyElementsException);
  }

  public T orElseThrow(Supplier<? extends RuntimeException> emptyExceptionSupplier,
      Supplier<? extends RuntimeException> multiExceptionSupplier) {
    return switch (size) {
      case 0 -> throw emptyExceptionSupplier.get();
      case 1 -> firstValue;
      default -> throw multiExceptionSupplier.get();
    };
  }

  public Stream<T> stream() {
    return stream(this::newTooManyElementsException);
  }

  public Stream<T> stream(Supplier<? extends RuntimeException> multiExceptionSupplier) {
    return switch (size) {
      case 0 -> Stream.empty();
      case 1 -> Stream.of(firstValue);
      default -> throw multiExceptionSupplier.get();
    };
  }

  public void ifPresent(Consumer<T> ifPresent,
      Supplier<? extends RuntimeException> multiExceptionSupplier) {
    ifPresentOrElse(ifPresent, () -> {
    }, multiExceptionSupplier);
  }

  public void ifPresentOrElse(Consumer<T> ifPresent, Runnable ifEmpty,
      Supplier<? extends RuntimeException> multiExceptionSupplier) {
    switch (size) {
      case 0 -> ifEmpty.run();
      case 1 -> ifPresent.accept(firstValue);
      default -> throw multiExceptionSupplier.get();
    }
  }

  public int size() {
    return size;
  }

  private RuntimeException newTooManyElementsException() {
    return new IllegalStateException("too many elements (" + size + ")");
  }
}
