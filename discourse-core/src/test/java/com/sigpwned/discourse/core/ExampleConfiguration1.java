package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;

@Configurable
public class ExampleConfiguration1 {
  @OptionParameter(shortName="a", longName="alpha", description="Alpha")
  public String optionAlpha;
  
  @OptionParameter(shortName="b", longName="bravo", description="Bravo", required=false)
  public boolean optionBravo;
  
  @OptionParameter(shortName="c", longName="charlie", description="Charlie", required=true)
  public int optionCharlie;
  
  @FlagParameter(shortName="d", longName="delta", description="Delta")
  public int switchDelta;
  
  @PositionalParameter(position=0, description="Echo")
  public int parameterEcho;
}
