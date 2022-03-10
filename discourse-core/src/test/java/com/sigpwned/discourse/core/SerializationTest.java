package com.sigpwned.discourse.core;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.Test;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;

/**
 * Tests every stock serializer
 */
public class SerializationTest {
  @Configurable
  public static class SerializationExample {
    // BIGDECIMAL /////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionBigDecimal")
    public BigDecimal optionBigDecimal;

    @EnvironmentParameter(variableName="VARIABLE_BIG_DECIMAL")
    public BigDecimal variableBigDecimal;

    @PropertyParameter(propertyName="property.bigDecimal")
    public BigDecimal propertyBigDecimal;
    
    // BOOLEAN ////////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionBooleanPrimitive")
    public boolean optionBooleanPrimitive;

    @EnvironmentParameter(variableName="VARIABLE_BOOLEAN_PRIMITIVE")
    public boolean variableBooleanPrimitive;

    @PropertyParameter(propertyName="property.boolean.primitive")
    public boolean propertyBooleanPrimitive;
  
    @OptionParameter(longName="optionBooleanObject")
    public Boolean optionBooleanObject;

    @EnvironmentParameter(variableName="VARIABLE_BOOLEAN_OBJECT")
    public Boolean variableBooleanObject;

    @PropertyParameter(propertyName="property.boolean.object")
    public Boolean propertyBooleanObject;
    
    // BYTE ///////////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionBytePrimitive")
    public byte optionBytePrimitive;

    @EnvironmentParameter(variableName="VARIABLE_BYTE_PRIMITIVE")
    public byte variableBytePrimitive;

    @PropertyParameter(propertyName="property.byte.primitive")
    public byte propertyBytePrimitive;
  
    @OptionParameter(longName="optionByteObject")
    public Byte optionByteObject;

    @EnvironmentParameter(variableName="VARIABLE_BYTE_OBJECT")
    public Byte variableByteObject;

    @PropertyParameter(propertyName="property.byte.object")
    public Byte propertyByteObject;
    
    // SHORT //////////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionShortPrimitive")
    public short optionShortPrimitive;

    @EnvironmentParameter(variableName="VARIABLE_SHORT_PRIMITIVE")
    public short variableShortPrimitive;

    @PropertyParameter(propertyName="property.short.primitive")
    public short propertyShortPrimitive;
  
    @OptionParameter(longName="optionShortObject")
    public Short optionShortObject;

    @EnvironmentParameter(variableName="VARIABLE_SHORT_OBJECT")
    public Short variableShortObject;

    @PropertyParameter(propertyName="property.short.object")
    public Short propertyShortObject;
    
    // INT ////////////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionIntPrimitive")
    public int optionIntPrimitive;

    @EnvironmentParameter(variableName="VARIABLE_INT_PRIMITIVE")
    public int variableIntPrimitive;

    @PropertyParameter(propertyName="property.int.primitive")
    public int propertyIntPrimitive;
  
    @OptionParameter(longName="optionIntObject")
    public Integer optionIntObject;

    @EnvironmentParameter(variableName="VARIABLE_INT_OBJECT")
    public Integer variableIntObject;

    @PropertyParameter(propertyName="property.int.object")
    public Integer propertyIntObject;
    
    // LONG ///////////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionLongPrimitive")
    public long optionLongPrimitive;

    @EnvironmentParameter(variableName="VARIABLE_LONG_PRIMITIVE")
    public long variableLongPrimitive;

    @PropertyParameter(propertyName="property.long.primitive")
    public long propertyLongPrimitive;
  
    @OptionParameter(longName="optionLongObject")
    public Long optionLongObject;

    @EnvironmentParameter(variableName="VARIABLE_LONG_OBJECT")
    public Long variableLongObject;

    @PropertyParameter(propertyName="property.long.object")
    public Long propertyLongObject;
    
    // FLOAT //////////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionFloatPrimitive")
    public float optionFloatPrimitive;

    @EnvironmentParameter(variableName="VARIABLE_FLOAT_PRIMITIVE")
    public float variableFloatPrimitive;

    @PropertyParameter(propertyName="property.float.primitive")
    public float propertyFloatPrimitive;
  
    @OptionParameter(longName="optionFloatObject")
    public Float optionFloatObject;

    @EnvironmentParameter(variableName="VARIABLE_FLOAT_OBJECT")
    public Float variableFloatObject;

    @PropertyParameter(propertyName="property.float.object")
    public Float propertyFloatObject;
    
    // DOUBLE /////////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionDoublePrimitive")
    public double optionDoublePrimitive;

    @EnvironmentParameter(variableName="VARIABLE_DOUBLE_PRIMITIVE")
    public double variableDoublePrimitive;

    @PropertyParameter(propertyName="property.double.primitive")
    public double propertyDoublePrimitive;
  
    @OptionParameter(longName="optionDoubleObject")
    public Double optionDoubleObject;

    @EnvironmentParameter(variableName="VARIABLE_DOUBLE_OBJECT")
    public Double variableDoubleObject;

    @PropertyParameter(propertyName="property.double.object")
    public Double propertyDoubleObject;
  
    // CHAR ///////////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionCharPrimitive")
    public char optionCharPrimitive;

    @EnvironmentParameter(variableName="VARIABLE_CHAR_PRIMITIVE")
    public char variableCharPrimitive;

    @PropertyParameter(propertyName="property.char.primitive")
    public char propertyCharPrimitive;
  
    @OptionParameter(longName="optionCharObject")
    public Character optionCharObject;

    @EnvironmentParameter(variableName="VARIABLE_CHAR_OBJECT")
    public Character variableCharObject;

    @PropertyParameter(propertyName="property.char.object")
    public Character propertyCharObject;
    
    // STRING /////////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionString")
    public String optionString;

    @EnvironmentParameter(variableName="VARIABLE_STRING")
    public String variableString;

    @PropertyParameter(propertyName="property.string")
    public String propertyString;
    
    // INSTANT ////////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionInstant")
    public String optionInstant;

    @EnvironmentParameter(variableName="VARIABLE_INSTANT")
    public String variableInstant;

    @PropertyParameter(propertyName="property.instant")
    public String propertyInstant;
    
    // LOCALDATE //////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionLocalDate")
    public LocalDate optionLocalDate;

    @EnvironmentParameter(variableName="VARIABLE_LOCAL_DATE")
    public LocalDate variableLocalDate;

    @PropertyParameter(propertyName="property.localDate")
    public LocalDate propertyLocalDate;
    
    // LOCALTIME //////////////////////////////////////////////////////////////
    @OptionParameter(longName="optionLocalTime")
    public LocalTime optionLocalTime;

    @EnvironmentParameter(variableName="VARIABLE_LOCAL_TIME")
    public LocalTime variableLocalTime;

    @PropertyParameter(propertyName="property.localTime")
    public LocalTime propertyLocalTime;
    
    // LOCALDATETIME //////////////////////////////////////////////////////////
    @OptionParameter(longName="optionLocalDateTime")
    public LocalDateTime optionLocalDateTime;

    @EnvironmentParameter(variableName="VARIABLE_LOCAL_DATE_TIME")
    public LocalDateTime variableLocalDateTime;

    @PropertyParameter(propertyName="property.localDateTime")
    public LocalDateTime propertyLocalDateTime;
  }
  
  @Test
  public void serializationTest() {
    new CommandBuilder().build(SerializationExample.class).args();
  }
}
