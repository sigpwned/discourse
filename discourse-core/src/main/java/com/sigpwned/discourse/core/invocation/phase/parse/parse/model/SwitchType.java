package com.sigpwned.discourse.core.invocation.phase.parse.parse.model;

public enum SwitchType {
  /**
   * A flag switch is a boolean-valued switch whose value is given by its presence or absence.
   */
  FLAG,

  /**
   * An option switch is a switch whose value is given explicitly.
   */
  OPTION;
}
