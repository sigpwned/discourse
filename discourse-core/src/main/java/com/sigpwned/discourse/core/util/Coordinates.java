package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.coordinate.Coordinate;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Coordinates {

  private Coordinates() {
  }

  public static BiConsumer<Coordinate, Consumer<NameCoordinate>> mapMultiName() {
    return mapMulti(NameCoordinate.class);
  }

  public static BiConsumer<Coordinate, Consumer<PositionCoordinate>> mapMultiPosition() {
    return mapMulti(PositionCoordinate.class);
  }

  public static <C extends Coordinate> BiConsumer<Coordinate, Consumer<C>> mapMulti(
      Class<C> clazz) {
    return (x, d) -> {
      if (clazz.isInstance(x)) {
        d.accept(clazz.cast(x));
      }
    };
  }
}
