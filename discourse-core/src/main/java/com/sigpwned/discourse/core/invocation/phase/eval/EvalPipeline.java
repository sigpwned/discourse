package com.sigpwned.discourse.core.invocation.phase.eval;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configurable3.rule.NamedRule;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EvalPipeline {

  private final EvalPhase evalPhase;
  private final FactoryPhase factoryPhase;
  private final EvalPipelineListener listener;

  public EvalPipeline(EvalPhase executePhase, FactoryPhase factoryPhase,
      EvalPipelineListener listener) {
    this.evalPhase = requireNonNull(executePhase);
    this.factoryPhase = requireNonNull(factoryPhase);
    this.listener = requireNonNull(listener);
  }

  public Object execute(Map<String, Object> initialState) {
    Map<String, Object> newState = evalPhase(initialState);

    Object instance = factoryPhase(newState);

    return instance;
  }

  protected Map<String, Object> evalPhase(Map<String, Object> initialState) {
    List<NamedRule> rules = new ArrayList<>();

    getListener().beforeEval(initialState, rules);

    Map<String, Object> newState = getEvalPhase().eval(initialState, rules);

    getListener().afterEval(initialState, rules, newState);

    return newState;
  }

  protected Object factoryPhase(Map<String, Object> newState) {
    getListener().beforeFactory(newState);

    Object instance = getFactoryPhase().getInstance(newState);

    getListener().afterFactory(newState, instance);

    return instance;
  }

  protected EvalPhase getEvalPhase() {
    return evalPhase;
  }

  protected FactoryPhase getFactoryPhase() {
    return factoryPhase;
  }

  protected EvalPipelineListener getListener() {
    return listener;
  }
}
