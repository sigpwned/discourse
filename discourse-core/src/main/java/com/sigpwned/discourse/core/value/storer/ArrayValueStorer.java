package com.sigpwned.discourse.core.value.storer;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import com.sigpwned.discourse.core.ValueStorer;
import com.sigpwned.discourse.core.util.Types;
import com.sigpwned.espresso.BeanProperty;

public class ArrayValueStorer implements ValueStorer {
  public static final ArrayValueStorer INSTANCE=new ArrayValueStorer();
  
  @Override
  public boolean isStoreable(BeanProperty property) {
    return Types.isConcreteArrayType(property.getGenericType());
  }

  @Override
  public void store(Object instance, BeanProperty property, Object value)
      throws InvocationTargetException {
    Object propertyValue = property.get(instance);

    // Make sure our property value has exactly one empty new value at the top of the array
    if (propertyValue == null) {
      property.set(instance,
          propertyValue = Types.newConcreteArrayInstance(property.getGenericType(), 1));
    } else {
      int length = Array.getLength(propertyValue);
      Object newPropertyValue =
          Types.newConcreteArrayInstance(property.getGenericType(), length + 1);
      System.arraycopy(propertyValue, 0, newPropertyValue, 0, length);
      property.set(instance, propertyValue = newPropertyValue);
    }

    Array.set(propertyValue, Array.getLength(propertyValue) - 1, value);
  }

  @Override
  public boolean isCollection() {
    return true;
  }
  
  @Override
  public int hashCode() {
    return 37;
  }
  
  @Override
  public boolean equals(Object other) {
    return getClass().equals(other.getClass());
  }

  @Override
  public String toString() {
    return "ArrayValueStorer []";
  }

  @Override
  public Type unpack(BeanProperty property) {
    // TODO Implement this, please
    throw new UnsupportedOperationException();
  }
}
