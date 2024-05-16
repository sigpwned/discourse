package com.sigpwned.discourse.core.invocation.phase.parse;

import java.util.List;
import java.util.Map;

public interface ParsePhaseListener {
  // PARSE STEP ///////////////////////////////////////////////////////////////////////////////////
  public void beforeParsePhaseParseStep(Map<String, String> vocabulary, List<String> args);

  public void afterParsePhaseParseStep(Map<String, String> vocabulary, List<String> args,
      List<Map.Entry<String, String>> parsedArgs);

  public void catchParsePhaseParseStep(Throwable problem);

  public void finallyParsePhaseParseStep();

  // ATTRIBUTE STEP ///////////////////////////////////////////////////////////////////////////////
  public void beforeParsePhaseAttributeStep(Map<String, String> naming,
      List<Map.Entry<String, String>> parsedArgs);

  public void afterParsePhaseAttributeStep(Map<String, String> naming,
      List<Map.Entry<String, String>> parsedArgs, List<Map.Entry<String, String>> attributedArgs);

  public void catchParsePhaseAttributeStep(Throwable problem);

  public void finallyParsePhaseAttributeStep();
}
