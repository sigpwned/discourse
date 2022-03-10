package com.sigpwned.discourse.examples.fizzbuzz.simple;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;

/**
 * This is the simplest configuration for the given parameters.
 */
@Configurable
public class SimpleFizzBuzzConfiguration {
    @PositionalParameter(position=0, required=true)
    public int count;
    
    @OptionParameter(shortName="f", longName="fizz")
    public String fizz = "fizz";
    
    @OptionParameter(shortName="b", longName="buzz")
    public String buzz = "buzz";
}