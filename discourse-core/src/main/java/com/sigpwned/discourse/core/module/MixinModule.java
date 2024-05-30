package com.sigpwned.discourse.core.module;

import static java.util.Objects.requireNonNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.annotation.DiscourseMixin;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializerFactory;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.ScanStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.NamingScheme;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleEvaluator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.util.JodaBeanUtils;
import com.sigpwned.discourse.core.util.Maybe;

public class MixinModule extends Module {
  // TODO what if someone annotations the field with @DiscourseMixin, but we have to use a setter?

  private static class MixinNomination {
    public final String prefix;
    public final String name;
    public final Object nominated;

    public MixinNomination(String prefix, String name, Object nominated) {
      this.prefix = requireNonNull(prefix);
      this.name = requireNonNull(name);
      this.nominated = requireNonNull(nominated);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, nominated);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      MixinNomination other = (MixinNomination) obj;
      return Objects.equals(name, other.name) && Objects.equals(nominated, other.nominated);
    }
  }

  private static class MixinCoordinate extends Coordinate {
    private final String prefix;
    private final String name;

    public MixinCoordinate(String prefix, String name) {
      this.prefix = requireNonNull(prefix);
      this.name = requireNonNull(name);
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
      return prefix;
    }

    /**
     * @return the name
     */
    public String getName() {
      return name;
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, prefix);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      MixinCoordinate other = (MixinCoordinate) obj;
      return Objects.equals(name, other.name) && Objects.equals(prefix, other.prefix);
    }

    @Override
    public String toString() {
      return "MixinCoordinate [prefix=" + prefix + ", name=" + name + "]";
    }
  }

  private Set<Class<?>> mixinTypes = new HashSet<>();

  @Override
  public void registerSyntaxNominators(final Chain<SyntaxNominator> syntaxNominatorChain) {
    syntaxNominatorChain.addLast(new SyntaxNominator() {
      private NamingScheme naming;
      private InvocationContext context;

      @Override
      public List<CandidateSyntax> nominateSyntax(Class<?> clazz, InvocationContext context) {
        this.naming = context.get(ScanStep.NAMING_SCHEME_KEY).orElseThrow();

        this.context = context;

        return walk(new ArrayList<>(), new ArrayList<>(), clazz);
      }


      private List<CandidateSyntax> walk(List<Class<?>> walking, List<String> lineage,
          Class<?> clazz) {
        if (walking.contains(clazz)) {
          walking.add(clazz);
          int index = walking.indexOf(clazz);
          throw new IllegalStateException(
              "circular mixin inheritance detected: " + walking.subList(index, walking.size()));
        }

        List<CandidateSyntax> result = new ArrayList<>();

        for (SyntaxNominator nominator : syntaxNominatorChain) {
          if (nominator == this)
            continue;

          List<CandidateSyntax> candidates = nominator.nominateSyntax(clazz, context);
          if (candidates == null || candidates.isEmpty())
            continue;

          for (CandidateSyntax candidate : candidates) {
            Maybe<String> maybeName = naming.name(candidate.nominated());
            if (!maybeName.isYes())
              continue;

            String name = maybeName.orElseThrow();

            if (candidate.annotations().stream().anyMatch(a -> a instanceof DiscourseMixin)) {
              result.add(new CandidateSyntax(
                  new MixinNomination(String.join(".", lineage), name, candidate.nominated()),
                  candidate.genericType(), candidate.annotations()));

              mixinTypes.add(JodaBeanUtils.eraseToClass(candidate.genericType()));

              walking.add(clazz);
              lineage.add(name);
              try {
                result.addAll(
                    walk(walking, lineage, JodaBeanUtils.eraseToClass(candidate.genericType())));
              } finally {
                walking.remove(walking.size() - 1);
                lineage.remove(lineage.size() - 1);
              }
            } else if (!lineage.isEmpty()) {
              result.add(new CandidateSyntax(
                  new MixinNomination(String.join(".", lineage), name, candidate.nominated()),
                  candidate.genericType(), candidate.annotations()));
            }
          }
        }

        return result;
      }
    });
  }

  @Override
  public void registerSyntaxDetectors(Chain<SyntaxDetector> syntaxDetectorChain) {
    syntaxDetectorChain.addLast(new SyntaxDetector() {
      @Override
      public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
          InvocationContext context) {
        if (!(candidate.nominated() instanceof MixinNomination mixin)) {
          return Maybe.maybe();
        }

        if (candidate.annotations().stream().anyMatch(a -> a instanceof DiscourseMixin)) {
          return Maybe.yes(new SyntaxDetection(false, false, false,
              Set.of(new MixinCoordinate(mixin.prefix, mixin.name))));
        }

        for (SyntaxDetector detector : syntaxDetectorChain) {
          if (detector == this)
            continue;
          Maybe<SyntaxDetection> result =
              detector.detectSyntax(clazz, new CandidateSyntax(mixin.nominated,
                  candidate.genericType(), candidate.annotations()), context);
          if (result.isDecided()) {
            return result;
          }
        }

        return Maybe.maybe();
      }
    });
  }

  @Override
  public void registerRuleNominators(Chain<RuleNominator> ruleNominatorChain) {
    ruleNominatorChain.addLast(new RuleNominator() {
      private NamingScheme naming;
      private InvocationContext context;

      @Override
      public List<CandidateRule> nominateRules(Class<?> clazz, List<NamedSyntax> syntax,
          InvocationContext context) {
        this.naming = context.get(ScanStep.NAMING_SCHEME_KEY).orElseThrow();

        this.context = context;

        return walk(new ArrayList<>(), new ArrayList<>(), clazz, syntax);
      }


      private List<CandidateRule> walk(List<Class<?>> walking, List<String> lineage, Class<?> clazz,
          List<NamedSyntax> syntax) {
        if (walking.contains(clazz)) {
          walking.add(clazz);
          int index = walking.indexOf(clazz);
          throw new IllegalStateException(
              "circular mixin inheritance detected: " + walking.subList(index, walking.size()));
        }

        List<CandidateRule> result = new ArrayList<>();

        for (RuleNominator nominator : ruleNominatorChain) {
          if (nominator == this)
            continue;

          List<CandidateRule> candidates = nominator.nominateRules(clazz, syntax, context);
          if (candidates == null || candidates.isEmpty())
            continue;

          for (CandidateRule candidate : candidates) {
            Maybe<String> maybeName = naming.name(candidate.nominated());
            if (!maybeName.isYes())
              continue;

            String name = maybeName.orElseThrow();

            if (candidate.annotations().stream().anyMatch(a -> a instanceof DiscourseMixin)) {
              result.add(new CandidateRule(
                  new MixinNomination(String.join(".", lineage), name, candidate.nominated()),
                  candidate.genericType(), candidate.annotations()));
              walking.add(clazz);
              lineage.add(name);
              try {
                result.addAll(walk(walking, lineage,
                    JodaBeanUtils.eraseToClass(candidate.genericType()), syntax));
              } finally {
                walking.remove(walking.size() - 1);
                lineage.remove(lineage.size() - 1);
              }
            } else if (!lineage.isEmpty()) {
              result.add(new CandidateRule(
                  new MixinNomination(String.join(".", lineage), name, candidate.nominated()),
                  candidate.genericType(), candidate.annotations()));
            }
          }
        }

        return result;
      }
    });
  }

  @Override
  public void registerRuleDetectors(Chain<RuleDetector> ruleDetectorChain) {
    ruleDetectorChain.addLast(new RuleDetector() {
      @Override
      public Maybe<RuleDetection> detectRule(Class<?> clazz, List<NamedSyntax> syntax,
          CandidateRule candidate, InvocationContext context) {
        if (!(candidate.nominated() instanceof MixinNomination mixin)) {
          return Maybe.maybe();
        }

        List<NamedSyntax> mixinSyntax = new ArrayList<>();
        for (NamedSyntax namedSyntax : syntax) {
          if (namedSyntax.nominated() instanceof MixinNomination mixin2) {
            mixinSyntax.add(new NamedSyntax(mixin2.nominated, namedSyntax.genericType(),
                namedSyntax.annotations(), namedSyntax.required(), namedSyntax.coordinates(),
                namedSyntax.name()));
          }
        }

        for (RuleDetector detector : ruleDetectorChain) {
          if (detector == this)
            continue;
          Maybe<RuleDetection> result = detector.detectRule(clazz, mixinSyntax,
              new CandidateRule(mixin.nominated, candidate.genericType(), candidate.annotations()),
              context);
          if (result.isNo()) {
            return Maybe.no();
          }
          if (result.isYes()) {
            RuleDetection detection = result.orElseThrow();

            Set<String> antecedents = new HashSet<>();
            for (String antecedent : detection.antecedents()) {
              if (antecedent.equals(""))
                antecedent = mixin.prefix;
              antecedents.add(antecedent);
            }

            Set<Set<String>> conditions = new HashSet<>();
            conditions.addAll(detection.conditions());
            for (NamedSyntax namedSyntax : mixinSyntax) {
              if (namedSyntax.name().startsWith(mixin.prefix + ".")) {
                conditions.add(Set.of(namedSyntax.name()));
              }
            }

            return Maybe.yes(new RuleDetection(antecedents, conditions, detection.hasConsequent()));
          }
        }

        return Maybe.maybe();
      }
    });
  }

  @Override
  public void registerRuleEvaluators(Chain<RuleEvaluator> ruleEvaluatorChain) {
    ruleEvaluatorChain.addLast(new RuleEvaluator() {
      @Override
      public Optional<Optional<Object>> run(Map<String, Object> input, NamedRule rule) {
        if (!(rule.nominated() instanceof MixinNomination mixin)) {
          return Optional.empty();
        }

        for (RuleEvaluator evaluator : ruleEvaluatorChain) {
          if (evaluator == this)
            continue;

          Optional<Optional<Object>> result =
              evaluator.run(input, new NamedRule(mixin.nominated, rule.genericType(),
                  rule.annotations(), rule.antecedents(), rule.conditions(), rule.consequent()));

          if (result.isPresent())
            return result;
        }

        return Optional.empty();
      }
    });
  }

  @Override
  public void registerNamingSchemes(Chain<NamingScheme> chain) {
    chain.addLast(new NamingScheme() {
      @Override
      public Maybe<String> name(Object object) {
        if (!(object instanceof MixinNomination mixin)) {
          return Maybe.maybe();
        }

        List<String> parts = new ArrayList<>(2);
        if (mixin.prefix != null && !mixin.prefix.isEmpty())
          parts.add(mixin.prefix);
        if (mixin.name != null && !mixin.name.isEmpty())
          parts.add(mixin.name);
        if (parts.isEmpty())
          parts.add("");

        return Maybe.yes(String.join(".", parts));
      }
    });
  }

  @Override
  public void registerValueDeserializerFactories(Chain<ValueDeserializerFactory<?>> chain) {
    chain.addLast(new ValueDeserializerFactory<Object>() {
      @Override
      public Optional<ValueDeserializer<? extends Object>> getDeserializer(Type genericType,
          List<Annotation> annotations) {
        if (mixinTypes.contains(JodaBeanUtils.eraseToClass(genericType))) {
          return Optional.of(s -> {
            throw new UnsupportedOperationException("cannot deserialize mixin type");
          });
        }
        return Optional.empty();
      }
    });
  }
}
