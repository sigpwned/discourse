/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.core;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.invocation.InvocationPipeline;

/**
 * Tests every stock serializer
 */
public class ValueDeserializersTest {
  public static enum ExampleEnum {
    HELLO, WORLD;
  }

  public static class ExampleFromString {

    public static ExampleFromString fromString(String s) {
      return null;
    }
  }

  @Configurable
  public static class SerializationExample {

    // BIGDECIMAL /////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionBigDecimal")
    public BigDecimal optionBigDecimal;

    @EnvironmentParameter(variableName = "VARIABLE_BIG_DECIMAL")
    public BigDecimal variableBigDecimal;

    @PropertyParameter(propertyName = "property.bigDecimal")
    public BigDecimal propertyBigDecimal;

    // BOOLEAN ////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionBooleanPrimitive")
    public boolean optionBooleanPrimitive;

    @EnvironmentParameter(variableName = "VARIABLE_BOOLEAN_PRIMITIVE")
    public boolean variableBooleanPrimitive;

    @PropertyParameter(propertyName = "property.boolean.primitive")
    public boolean propertyBooleanPrimitive;

    @OptionParameter(longName = "optionBooleanObject")
    public Boolean optionBooleanObject;

    @EnvironmentParameter(variableName = "VARIABLE_BOOLEAN_OBJECT")
    public Boolean variableBooleanObject;

    @PropertyParameter(propertyName = "property.boolean.object")
    public Boolean propertyBooleanObject;

    // BYTE ///////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionBytePrimitive")
    public byte optionBytePrimitive;

    @EnvironmentParameter(variableName = "VARIABLE_BYTE_PRIMITIVE")
    public byte variableBytePrimitive;

    @PropertyParameter(propertyName = "property.byte.primitive")
    public byte propertyBytePrimitive;

    @OptionParameter(longName = "optionByteObject")
    public Byte optionByteObject;

    @EnvironmentParameter(variableName = "VARIABLE_BYTE_OBJECT")
    public Byte variableByteObject;

    @PropertyParameter(propertyName = "property.byte.object")
    public Byte propertyByteObject;

    // SHORT //////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionShortPrimitive")
    public short optionShortPrimitive;

    @EnvironmentParameter(variableName = "VARIABLE_SHORT_PRIMITIVE")
    public short variableShortPrimitive;

    @PropertyParameter(propertyName = "property.short.primitive")
    public short propertyShortPrimitive;

    @OptionParameter(longName = "optionShortObject")
    public Short optionShortObject;

    @EnvironmentParameter(variableName = "VARIABLE_SHORT_OBJECT")
    public Short variableShortObject;

    @PropertyParameter(propertyName = "property.short.object")
    public Short propertyShortObject;

    // INT ////////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionIntPrimitive")
    public int optionIntPrimitive;

    @EnvironmentParameter(variableName = "VARIABLE_INT_PRIMITIVE")
    public int variableIntPrimitive;

    @PropertyParameter(propertyName = "property.int.primitive")
    public int propertyIntPrimitive;

    @OptionParameter(longName = "optionIntObject")
    public Integer optionIntObject;

    @EnvironmentParameter(variableName = "VARIABLE_INT_OBJECT")
    public Integer variableIntObject;

    @PropertyParameter(propertyName = "property.int.object")
    public Integer propertyIntObject;

    // LONG ///////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionLongPrimitive")
    public long optionLongPrimitive;

    @EnvironmentParameter(variableName = "VARIABLE_LONG_PRIMITIVE")
    public long variableLongPrimitive;

    @PropertyParameter(propertyName = "property.long.primitive")
    public long propertyLongPrimitive;

    @OptionParameter(longName = "optionLongObject")
    public Long optionLongObject;

    @EnvironmentParameter(variableName = "VARIABLE_LONG_OBJECT")
    public Long variableLongObject;

    @PropertyParameter(propertyName = "property.long.object")
    public Long propertyLongObject;

    // FLOAT //////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionFloatPrimitive")
    public float optionFloatPrimitive;

    @EnvironmentParameter(variableName = "VARIABLE_FLOAT_PRIMITIVE")
    public float variableFloatPrimitive;

    @PropertyParameter(propertyName = "property.float.primitive")
    public float propertyFloatPrimitive;

    @OptionParameter(longName = "optionFloatObject")
    public Float optionFloatObject;

    @EnvironmentParameter(variableName = "VARIABLE_FLOAT_OBJECT")
    public Float variableFloatObject;

    @PropertyParameter(propertyName = "property.float.object")
    public Float propertyFloatObject;

    // DOUBLE /////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionDoublePrimitive")
    public double optionDoublePrimitive;

    @EnvironmentParameter(variableName = "VARIABLE_DOUBLE_PRIMITIVE")
    public double variableDoublePrimitive;

    @PropertyParameter(propertyName = "property.double.primitive")
    public double propertyDoublePrimitive;

    @OptionParameter(longName = "optionDoubleObject")
    public Double optionDoubleObject;

    @EnvironmentParameter(variableName = "VARIABLE_DOUBLE_OBJECT")
    public Double variableDoubleObject;

    @PropertyParameter(propertyName = "property.double.object")
    public Double propertyDoubleObject;

    // CHAR ///////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionCharPrimitive")
    public char optionCharPrimitive;

    @EnvironmentParameter(variableName = "VARIABLE_CHAR_PRIMITIVE")
    public char variableCharPrimitive;

    @PropertyParameter(propertyName = "property.char.primitive")
    public char propertyCharPrimitive;

    @OptionParameter(longName = "optionCharObject")
    public Character optionCharObject;

    @EnvironmentParameter(variableName = "VARIABLE_CHAR_OBJECT")
    public Character variableCharObject;

    @PropertyParameter(propertyName = "property.char.object")
    public Character propertyCharObject;

    // STRING /////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionString")
    public String optionString;

    @EnvironmentParameter(variableName = "VARIABLE_STRING")
    public String variableString;

    @PropertyParameter(propertyName = "property.string")
    public String propertyString;

    // INSTANT ////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionInstant")
    public String optionInstant;

    @EnvironmentParameter(variableName = "VARIABLE_INSTANT")
    public String variableInstant;

    @PropertyParameter(propertyName = "property.instant")
    public String propertyInstant;

    // LOCALDATE //////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionLocalDate")
    public LocalDate optionLocalDate;

    @EnvironmentParameter(variableName = "VARIABLE_LOCAL_DATE")
    public LocalDate variableLocalDate;

    @PropertyParameter(propertyName = "property.localDate")
    public LocalDate propertyLocalDate;

    // LOCALTIME //////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionLocalTime")
    public LocalTime optionLocalTime;

    @EnvironmentParameter(variableName = "VARIABLE_LOCAL_TIME")
    public LocalTime variableLocalTime;

    @PropertyParameter(propertyName = "property.localTime")
    public LocalTime propertyLocalTime;

    // LOCALDATETIME //////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionLocalDateTime")
    public LocalDateTime optionLocalDateTime;

    @EnvironmentParameter(variableName = "VARIABLE_LOCAL_DATE_TIME")
    public LocalDateTime variableLocalDateTime;

    @PropertyParameter(propertyName = "property.localDateTime")
    public LocalDateTime propertyLocalDateTime;

    // URL ////////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionUrl")
    public URL optionUrl;

    @EnvironmentParameter(variableName = "VARIABLE_URL")
    public URL variableUrl;

    @PropertyParameter(propertyName = "property.url")
    public URL propertyUrl;

    // ENUM ///////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionEnum")
    public ExampleEnum optionEnum;

    @EnvironmentParameter(variableName = "VARIABLE_ENUM")
    public ExampleEnum variableEnum;

    @PropertyParameter(propertyName = "property.enum")
    public ExampleEnum propertyEnum;

    // FILE ///////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionFile")
    public File optionFile;

    @EnvironmentParameter(variableName = "VARIABLE_FILE")
    public File variableFile;

    @PropertyParameter(propertyName = "property.file")
    public File propertyFile;

    // PATH ///////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionPath")
    public Path optionPath;

    @EnvironmentParameter(variableName = "VARIABLE_PATH")
    public Path variablePath;

    @PropertyParameter(propertyName = "property.path")
    public Path propertyPath;

    // PATTERN ////////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionPattern")
    public Pattern optionPattern;

    @EnvironmentParameter(variableName = "VARIABLE_PATTERN")
    public Pattern variablePattern;

    @PropertyParameter(propertyName = "property.pattern")
    public Pattern propertyPattern;

    // FROMSTRING /////////////////////////////////////////////////////////////
    @OptionParameter(longName = "optionFromString")
    public ExampleFromString optionFromString;

    @EnvironmentParameter(variableName = "VARIABLE_FROM_STRING")
    public ExampleFromString variableFromString;

    @PropertyParameter(propertyName = "property.fromString")
    public ExampleFromString propertyFromString;
  }

  @Test
  @SuppressWarnings("unused")
  public void givenConfigurationClassWithParametersOfAllStockTypes_whenInvoke_thenSucceed() {
    SerializationExample example = InvocationPipeline.builder().build()
        .execute(SerializationExample.class, List.of()).getInstance();
  }
}
