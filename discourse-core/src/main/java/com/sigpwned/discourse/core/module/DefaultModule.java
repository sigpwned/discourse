package com.sigpwned.discourse.core.module;

import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.core.SinkContext;
import com.sigpwned.discourse.core.value.deserializer.BigDecimalValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.BooleanValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.ByteValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.CharValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.DoubleValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.EnumValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.FileValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.FloatValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.FromStringValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.InstantValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.IntValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LocalDateTimeValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LocalDateValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LocalTimeValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.LongValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.PathValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.ShortValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.StringValueDeserializerFactory;
import com.sigpwned.discourse.core.value.deserializer.UrlValueDeserializerFactory;
import com.sigpwned.discourse.core.value.sink.ArrayAppendValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.ListAddValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.SetAddValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.SortedSetAddValueSinkFactory;

public class DefaultModule extends Module {
  @Override
  public void register(SerializationContext context) {
    context.addLast(StringValueDeserializerFactory.INSTANCE);
    context.addLast(LongValueDeserializerFactory.INSTANCE);
    context.addLast(IntValueDeserializerFactory.INSTANCE);
    context.addLast(CharValueDeserializerFactory.INSTANCE);
    context.addLast(ShortValueDeserializerFactory.INSTANCE);
    context.addLast(ByteValueDeserializerFactory.INSTANCE);
    context.addLast(DoubleValueDeserializerFactory.INSTANCE);
    context.addLast(FloatValueDeserializerFactory.INSTANCE);
    context.addLast(BigDecimalValueDeserializerFactory.INSTANCE);
    context.addLast(BooleanValueDeserializerFactory.INSTANCE);
    context.addLast(InstantValueDeserializerFactory.INSTANCE);
    context.addLast(LocalDateTimeValueDeserializerFactory.INSTANCE);
    context.addLast(LocalDateValueDeserializerFactory.INSTANCE);
    context.addLast(LocalTimeValueDeserializerFactory.INSTANCE);
    context.addLast(UrlValueDeserializerFactory.INSTANCE);
    context.addLast(EnumValueDeserializerFactory.INSTANCE);
    context.addLast(FileValueDeserializerFactory.INSTANCE);
    context.addLast(PathValueDeserializerFactory.INSTANCE);

    // This should be the last resort.
    context.addLast(FromStringValueDeserializerFactory.INSTANCE);
  }

  @Override
  public void register(SinkContext context) {
    context.addLast(SortedSetAddValueSinkFactory.INSTANCE);
    context.addLast(SetAddValueSinkFactory.INSTANCE);
    context.addLast(ListAddValueSinkFactory.INSTANCE);
    context.addLast(ArrayAppendValueSinkFactory.INSTANCE);
  }
}
