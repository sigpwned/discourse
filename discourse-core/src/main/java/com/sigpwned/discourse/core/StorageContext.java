package com.sigpwned.discourse.core;

import static java.util.Collections.unmodifiableList;
import java.util.LinkedList;
import java.util.List;
import com.sigpwned.discourse.core.value.storer.ArrayValueStorer;
import com.sigpwned.discourse.core.value.storer.AssignValueStorer;
import com.sigpwned.discourse.core.value.storer.ListValueStorer;
import com.sigpwned.discourse.core.value.storer.SetValueStorer;
import com.sigpwned.discourse.core.value.storer.SortedSetValueStorer;
import com.sigpwned.espresso.BeanProperty;

public class StorageContext {
  private final LinkedList<ValueStorer> storers;
  private final ValueStorer defaultStorer;

  public StorageContext() {
    this(AssignValueStorer.INSTANCE);
  }

  public StorageContext(ValueStorer defaultStorer) {
    if(defaultStorer == null)
      throw new NullPointerException();
    this.storers = new LinkedList<>();
    this.defaultStorer = defaultStorer;
    
    addLast(SortedSetValueStorer.INSTANCE);
    addLast(SetValueStorer.INSTANCE);
    addLast(ListValueStorer.INSTANCE);
    addLast(ArrayValueStorer.INSTANCE);
  }
  
  public ValueStorer getStorer(BeanProperty property) {
    return storers.stream().filter(d -> d.isStoreable(property))
        .findFirst()
        .orElse(getDefaultStorer());
  }
  

  public void addFirst(ValueStorer deserializer) {
    storers.remove(deserializer);
    storers.addFirst(deserializer);
  }

  public void addLast(ValueStorer storer) {
    storers.remove(storer);
    storers.addLast(storer);
  }

  public List<ValueStorer> getStorers() {
    return unmodifiableList(storers);
  }

  /**
   * @return the defaultStorer
   */
  private ValueStorer getDefaultStorer() {
    return defaultStorer;
  }
}
