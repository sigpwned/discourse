package com.sigpwned.discourse.core.pipeline.invocation.step;

import static com.sigpwned.discourse.core.util.MoreCollectors.duplicates;
import static java.util.stream.Collectors.toSet;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.DiscourseDefaultValue;
import com.sigpwned.discourse.core.annotation.DiscourseDescription;
import com.sigpwned.discourse.core.annotation.DiscourseExampleValue;
import com.sigpwned.discourse.core.annotation.DiscourseRequired;
import com.sigpwned.discourse.core.command.Discriminator;
import com.sigpwned.discourse.core.command.tree.Command;
import com.sigpwned.discourse.core.command.tree.LeafCommand;
import com.sigpwned.discourse.core.command.tree.LeafCommandProperty;
import com.sigpwned.discourse.core.command.tree.RootCommand;
import com.sigpwned.discourse.core.exception.InternalDiscourseException;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStepBase;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.NamingScheme;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleEvaluator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RuleNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.RulesEngine;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SubCommandScanner;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxNominator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DiscriminatorMismatchScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DivergentRulesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicateCoordinatesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicateDiscriminatorsScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicatePropertyNamesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicateRuleNomineesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicateSyntaxNamesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.DuplicateSyntaxNomineesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.InsufficientRulesScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.InvalidDiscriminatorScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.LeafCommandAbstractScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.LeafCommandMissingBodyScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.NoDiscriminatorScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.NotConfigurableScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.SubCommandDoesNotExtendSuperCommandScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.SuperCommandNotAbstractScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.UnexpectedDiscriminatorScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.UnnamedRuleConsequentScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception.UnnamedSyntaxScanException;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CommandBody;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.DetectedRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.DetectedSyntax;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedSyntax;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.PreparedClass;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.RuleDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.SuperCommand;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.SyntaxDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.WalkedClass;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.util.MoreRules;
import com.sigpwned.discourse.core.util.Graphs;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.MoreSets;
import com.sigpwned.discourse.core.util.Streams;

public class ScanStep extends InvocationPipelineStepBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScanStep.class);

  public static final InvocationContext.Key<SubCommandScanner> SUB_COMMAND_SCANNER_KEY =
      InvocationContext.Key.of(SubCommandScanner.class);

  public static final InvocationContext.Key<NamingScheme> NAMING_SCHEME_KEY =
      InvocationContext.Key.of(NamingScheme.class);

  public static final InvocationContext.Key<SyntaxNominator> SYNTAX_NOMINATOR_KEY =
      InvocationContext.Key.of(SyntaxNominator.class);

  public static final InvocationContext.Key<SyntaxDetector> SYNTAX_DETECTOR_KEY =
      InvocationContext.Key.of(SyntaxDetector.class);

  public static final InvocationContext.Key<RuleNominator> RULE_NOMINATOR_KEY =
      InvocationContext.Key.of(RuleNominator.class);

  public static final InvocationContext.Key<RuleDetector> RULE_DETECTOR_KEY =
      InvocationContext.Key.of(RuleDetector.class);

  public static final InvocationContext.Key<RuleEvaluator> RULE_EVALUATOR_KEY =
      InvocationContext.Key.of(RuleEvaluator.class);

  public final <T> RootCommand<T> scan(Class<T> clazz, InvocationContext context) {
    RootCommand<T> tree;

    try {
      getListener(context).beforeScanStep(clazz, context);
      tree = doScan(clazz, context);
      getListener(context).afterScanStep(clazz, tree, context);
    } catch (Throwable t) {
      getListener(context).catchScanStep(t, context);
      throw t;
    } finally {
      getListener(context).finallyScanStep(context);
    }

    return tree;
  }

  private <T> RootCommand<T> doScan(Class<T> clazz, InvocationContext context) {
    List<WalkedClass<? extends T>> walkedClasses = doWalkStep(clazz, context);

    List<PreparedClass<? extends T>> preparedClasses = doPrepareStep(walkedClasses, context);

    RootCommand<T> tree = doTreeStep(preparedClasses, context);

    return tree;
  }

  private <T> List<WalkedClass<? extends T>> doWalkStep(Class<T> clazz, InvocationContext context) {
    SubCommandScanner scanner = context.get(SUB_COMMAND_SCANNER_KEY).orElseThrow();

    List<WalkedClass<? extends T>> walkedClasses;
    try {
      getListener(context).beforeScanStepWalk(clazz, context);
      walkedClasses = walkStep(scanner, clazz, context);
      getListener(context).afterScanStepWalk(clazz, walkedClasses, context);
    } catch (Throwable t) {
      getListener(context).catchScanStepWalk(t, context);
      throw t;
    } finally {
      getListener(context).finallyScanStepWalk(context);
    }
    return walkedClasses;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected <T> List<WalkedClass<? extends T>> walkStep(SubCommandScanner scanner, Class<T> clazz,
      InvocationContext context) {
    record WalkingClass<T>(Optional<SuperCommand<? super T>> supercommand, Class<T> clazz) {
    }

    List<WalkedClass<? extends T>> walkedClasses = new ArrayList<>();

    Queue<WalkingClass<? extends T>> queue = new LinkedList<>();
    queue.add(new WalkingClass<>(Optional.empty(), clazz));
    do {
      WalkingClass<? extends T> current = queue.poll();
      SuperCommand<? super T> supercommand = (SuperCommand) current.supercommand().orElse(null);
      Class<? extends T> currentClazz = (Class) current.clazz();

      // It's a hard requirement that all command classes have a @Configurable annotation. This is
      // how we know that a class is a command class, and it's how we know what the discriminator
      // is, plus various other metadata.
      Configurable configurable = currentClazz.getAnnotation(Configurable.class);
      if (configurable == null) {
        throw new NotConfigurableScanException(currentClazz);
      }

      // We need to validate some things about our command structure, depending on whether or not
      // we have a supercommand.
      if (supercommand != null) {
        // This is an important test. We want to ensure that the superclazz is actually a superclass
        // of the clazz to enforce that the result of a subcommand is assignable to the type of the
        // supercommand. This is a useful invariant, but it also guarantees that there are no cycles
        // in the inheritance graph.
        if (!supercommand.clazz().isAssignableFrom(currentClazz)) {
          throw new SubCommandDoesNotExtendSuperCommandScanException(supercommand.clazz(),
              currentClazz);
        }

        // If we have a supercommand, then we need to have a discriminator.
        if (configurable.discriminator().isEmpty()) {
          throw new NoDiscriminatorScanException(currentClazz);
        }

        // If we have a supercommand, then we need to ensure that the discriminator matches the
        // discriminator of the supercommand.
        Discriminator configurableDiscriminator;
        try {
          configurableDiscriminator = Discriminator.of(configurable.discriminator());
        } catch (IllegalArgumentException e) {
          throw new InvalidDiscriminatorScanException(currentClazz, configurable.discriminator());
        }
        if (!configurableDiscriminator.equals(supercommand.discriminator())) {
          throw new DiscriminatorMismatchScanException(currentClazz, supercommand.discriminator(),
              configurableDiscriminator);
        }
      } else {
        // If we don't have a supercommand, then we should not have a discriminator.
        if (!configurable.discriminator().isEmpty()) {
          throw new UnexpectedDiscriminatorScanException(currentClazz);
        }
      }

      List<Map.Entry<String, Class<? extends T>>> subcommands =
          (List) scanner.scanForSubCommands(currentClazz).orElseGet(Collections::emptyList);

      // A Command with SubCommands (i.e., a SuperCommand) has some special requirements. A Command
      // with no SubCommands has some special requirements, too. Let's enforce them both here.
      if (subcommands.isEmpty()) {
        // This is a leaf command. Leaf commands must not be abstract.
        if (Modifier.isAbstract(currentClazz.getModifiers())) {
          throw new LeafCommandAbstractScanException(currentClazz);
        }
      } else {
        // This is a super command. Super commands must be abstract.
        if (!Modifier.isAbstract(currentClazz.getModifiers())) {
          throw new SuperCommandNotAbstractScanException(currentClazz);
        }
      }

      walkedClasses
          .add(new WalkedClass(Optional.ofNullable(supercommand), currentClazz, configurable));

      for (Map.Entry<String, Class<? extends T>> e : subcommands) {
        // This is a little ticklish. Depending on the subcommand scanner(s) in use, the
        // discriminator might come from the class itself, or it might come from the subcommand. We
        // do our best providing a pointer to the offending discriminator.
        Discriminator expectedDiscriminator;
        try {
          expectedDiscriminator = Discriminator.of(e.getKey());
        } catch (IllegalArgumentException x) {
          throw new InvalidDiscriminatorScanException(currentClazz, e.getKey());
        }

        Class<? extends T> subcommand = e.getValue();

        queue.add(new WalkingClass(
            Optional.of(new SuperCommand<>(currentClazz, expectedDiscriminator)), subcommand));
      }
    } while (!queue.isEmpty());

    // For each class, we need to ensure that there are no duplicate discriminators. This is a
    // requirement for the command hierarchy to be well-defined. It's fine if two different
    // supercommands have subcommands with the same distriminator. It's not fine if one supercommand
    // has two subcommands with the same discriminator.
    for (WalkedClass<? extends T> walkedClass : walkedClasses) {
      // Let's look for all subcommands of this class.
      final Class<?> superclazz = walkedClass.clazz();

      // Here are the subcommands of this class.
      List<WalkedClass<?>> subcommands =
          (List) walkedClasses.stream().filter(wc -> wc.supercommand().isPresent())
              .filter(wc -> wc.supercommand().orElseThrow().clazz() == superclazz).toList();

      // Are there any duplicate discriminators?
      subcommands.stream().map(sc -> sc.supercommand().orElseThrow().discriminator())
          .collect(duplicates()).ifPresent(duplicateDiscriminators -> {
            throw new DuplicateDiscriminatorsScanException(superclazz, duplicateDiscriminators);
          });
    }

    return walkedClasses;
  }

  private <T> List<PreparedClass<? extends T>> doPrepareStep(
      List<WalkedClass<? extends T>> walkedClasses, InvocationContext context) {
    NamingScheme naming = context.get(NAMING_SCHEME_KEY).orElseThrow();

    SyntaxNominator syntaxNominator = context.get(SYNTAX_NOMINATOR_KEY).orElseThrow();

    SyntaxDetector syntaxDetector = context.get(SYNTAX_DETECTOR_KEY).orElseThrow();

    RuleNominator ruleNominator = context.get(RULE_NOMINATOR_KEY).orElseThrow();

    RuleDetector ruleDetector = context.get(RULE_DETECTOR_KEY).orElseThrow();

    List<PreparedClass<? extends T>> preparedClasses;
    try {
      getListener(context).beforeScanStepPrepare(walkedClasses, context);
      preparedClasses = prepareStep(naming, syntaxNominator, syntaxDetector, ruleNominator,
          ruleDetector, walkedClasses, context);
      getListener(context).afterScanStepPrepare(walkedClasses, preparedClasses, context);
    } catch (Throwable t) {
      getListener(context).catchScanStepPrepare(t, context);
      throw t;
    } finally {
      getListener(context).finallyScanStepPrepare(context);
    }
    return preparedClasses;
  }

  protected <T> List<PreparedClass<? extends T>> prepareStep(NamingScheme naming,
      SyntaxNominator syntaxNominator, SyntaxDetector syntaxDetector, RuleNominator ruleNominator,
      RuleDetector ruleDetector, List<WalkedClass<? extends T>> walkedClasses,
      InvocationContext context) {
    List<PreparedClass<? extends T>> preparedClasses = new ArrayList<>(walkedClasses.size());

    for (WalkedClass<? extends T> walkedClass : walkedClasses) {
      boolean hasSubcommands = walkedClasses.stream().flatMap(wc -> wc.supercommand().stream())
          .anyMatch(sc -> sc.clazz().equals(walkedClass.clazz()));

      CommandBody body;
      if (hasSubcommands) {
        // If a class has subcommands, we don't want to create a body for it. We'll create a body
        // for its subcommands instead.
        body = null;
      } else {
        // If a class doesn't have subcommands, we'll create a body for it.
        Class<?> clazz = walkedClass.clazz();

        ///////////////////////////////////////////////////////////////////////////////////////////
        // SYNTAX /////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////

        // Nominate all our candidate syntax. That is, we want to identify all the various and
        // sundry class members that might be syntax-bearing.
        List<CandidateSyntax> candidateSyntax = syntaxNominator.nominateSyntax(clazz, context);

        // Let's deduplicate our candidates at the record level. We don't want to process
        // duplicates. In theory, it's an error if our syntax nominators nominate the same thing
        // twice, but if we can just remove duplicates and carry on with our lives, then we should.
        if (new HashSet<>(candidateSyntax).size() != candidateSyntax.size()) {
          // Welp, we have duplicates. Remove them, preserving order, and log an error.
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Ignoring exact duplicates for syntax nominees {}",
                Streams.duplicates(candidateSyntax.stream()).map(CandidateSyntax::nominated)
                    .collect(toSet()));
          candidateSyntax = candidateSyntax.stream().distinct().toList();
        }

        // If we still have duplicates at the nominee level after removing duplicates at the record
        // level, then we have a problem.
        candidateSyntax.stream().map(CandidateSyntax::nominated).collect(duplicates())
            .ifPresent(duplicateSyntaxNominees -> {
              throw new DuplicateSyntaxNomineesScanException(clazz, duplicateSyntaxNominees);
            });

        // Detect all the syntax. That is, we want to identify all the syntax-bearing class members
        // from among the candidates that are actually syntax. The detector must preserve the
        // nominee from the candidate exactly. If the detector nominates something different, then
        // an exception is thrown.
        List<DetectedSyntax> detectedSyntax = new ArrayList<>();
        for (CandidateSyntax csi : candidateSyntax) {
          Maybe<SyntaxDetection> maybeSyntaxDetection =
              syntaxDetector.detectSyntax(clazz, csi, context);
          if (maybeSyntaxDetection.isYes()) {
            detectedSyntax.add(
                DetectedSyntax.fromCandidateAndDetection(csi, maybeSyntaxDetection.orElseThrow()));
          } else {
            // This is fine. Not every candidate is actually syntax.
          }
        }

        // Now that we have all our syntax, let's name it.
        List<NamedSyntax> syntax = new ArrayList<>();
        for (DetectedSyntax dsi : detectedSyntax) {
          Maybe<String> maybeName = naming.name(dsi.nominated());
          if (maybeName.isYes()) {
            syntax.add(NamedSyntax.fromDetectedSyntax(dsi, maybeName.orElseThrow()));
          } else if (maybeName.isNo()) {
            // It's hard to think of a situation where a syntax element would be rejected at the
            // naming stage, but if it happens, we should log it and carry on.
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Ignoring syntax nominee {} due to naming rejection", dsi.nominated());
          } else {
            // This is not OK. Everything has to be named.
            throw new UnnamedSyntaxScanException(clazz, dsi.nominated());
          }
        }

        // Did we end up with any duplicate names?
        syntax.stream().map(NamedSyntax::name).collect(duplicates())
            .ifPresent(duplicateSyntaxNames -> {
              throw new DuplicateSyntaxNamesScanException(clazz, duplicateSyntaxNames);
            });

        ///////////////////////////////////////////////////////////////////////////////////////////
        // RULES //////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////

        // Nominate all our candidate rules. That is, we want to identify all the various and
        // sundry class members that might be rules.
        List<CandidateRule> candidateRules = ruleNominator.nominateRules(clazz, syntax, context);

        // Let's deduplicate our candidates at the record level. We don't want to process
        // duplicates. In theory, it's an error if our syntax nominators nominate the same thing
        // twice, but if we can just remove duplicates and carry on with our lives, then we should.
        if (new HashSet<>(candidateRules).size() != candidateRules.size()) {
          // Welp, we have duplicates. Remove them, preserving order, and log an error.
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Ignoring exact duplicates for rule nominees {}",
                Streams.duplicates(candidateRules.stream()).map(CandidateRule::nominated)
                    .collect(toSet()));
          candidateRules = candidateRules.stream().distinct().toList();
        }

        // If we still have duplicates at the nominee level after removing duplicates at the record
        // level, then we have a problem.
        candidateRules.stream().map(CandidateRule::nominated).collect(duplicates())
            .ifPresent(duplicateRuleNominees -> {
              throw new DuplicateRuleNomineesScanException(clazz, duplicateRuleNominees);
            });

        // Detect all the rules. That is, we want to identify all the rule-bearing class members
        // from among the candidates that are actually rules.
        List<DetectedRule> detectedRules = new ArrayList<>();
        for (CandidateRule cri : candidateRules) {
          Maybe<RuleDetection> maybeRuleDetection =
              ruleDetector.detectRule(clazz, syntax, cri, context);
          if (maybeRuleDetection.isYes()) {
            detectedRules
                .add(DetectedRule.fromCandidateAndDetection(cri, maybeRuleDetection.orElseThrow()));
          } else {
            // This is fine. Not every candidate is actually a rule.
          }
        }

        // Now that we have all our rules, let's name them.
        List<NamedRule> rules = new ArrayList<>();
        for (DetectedRule dri : detectedRules) {
          if (dri.hasConsequent()) {
            // We only name consequents. Antecedents come named. So we only need to name the
            // rule if it has a consequent.
            Maybe<String> maybeName = naming.name(dri.nominated());
            if (maybeName.isYes()) {
              rules.add(new NamedRule(dri.humanReadableName(), dri.nominated(), dri.genericType(),
                  dri.annotations(), dri.antecedents(), dri.conditions(),
                  Optional.of(maybeName.orElseThrow())));
            } else if (maybeName.isNo()) {
              // It's hard to think of a situation where a rule would be rejected at the naming
              // stage, but if it happens, we should log it and carry on.
              if (LOGGER.isDebugEnabled())
                LOGGER.debug("Ignoring rule nominee {} due to naming rejection", dri.nominated());
            } else {
              // This is not OK. Everything has to be named.
              throw new UnnamedRuleConsequentScanException(clazz, dri.nominated());
            }
          } else {
            // No consequent, no name.
            rules.add(new NamedRule(dri.humanReadableName(), dri.nominated(), dri.genericType(),
                dri.annotations(), dri.antecedents(), dri.conditions(), Optional.empty()));
          }
        }

        // Duplicate rules are fine. We let the reactor sort it out.

        // TODO Where does the description come from?
        List<LeafCommandProperty> properties = new ArrayList<>();
        for (NamedSyntax namedSyntax : syntax) {
          // TODO Should this be a pluggable implementation?
          String description = namedSyntax.annotations().stream()
              .mapMulti(Streams.filterAndCast(DiscourseDescription.class)).findFirst()
              .map(DiscourseDescription::value).orElse(null);
          // TODO Should this be a pluggable implementation? For example, @NonNull?
          boolean required = namedSyntax.annotations().stream()
              .mapMulti(Streams.filterAndCast(DiscourseRequired.class)).findFirst().isPresent();

          // TODO Should this be a pluggable implementation?
          String defaultValue = namedSyntax.annotations().stream()
              .mapMulti(Streams.filterAndCast(DiscourseDefaultValue.class)).findFirst()
              .map(DiscourseDefaultValue::value).orElse(null);

          // TODO Should this be a pluggable implementation?
          String exampleValue = namedSyntax.annotations().stream()
              .mapMulti(Streams.filterAndCast(DiscourseExampleValue.class)).findFirst()
              .map(DiscourseExampleValue::value).orElse(null);
          if (exampleValue == null)
            exampleValue = defaultValue;

          properties.add(new LeafCommandProperty(namedSyntax.name(), description, required,
              defaultValue, exampleValue, namedSyntax.coordinates(), namedSyntax.genericType(),
              namedSyntax.annotations()));
        }

        // TODO Should we defer validation of the rules until later? Developers may customize after.
        // Do we have a clear winner for evaluation and construction?
        Set<String> allPropertyNames = new HashSet<>();
        for (LeafCommandProperty property : properties)
          allPropertyNames.add(property.getName());

        // We sort by the highest number of consumed properties first, then by the lowest number of
        // evaluated rules. Give me the most bang (side effects) for the least buck (work).
        List<MoreRules.Reaction> reactions = MoreRules.react(rules, allPropertyNames).stream()
            .sorted(Comparator.<MoreRules.Reaction>comparingInt(ri -> -ri.consumed().size())
                .thenComparingInt(ri -> ri.evaluated().size()))
            .toList();
        if (reactions.isEmpty()) {
          // I'm not even sure how this would happen. A class with no fields and no default
          // constructor? We should catch that earlier. But better safe...
          throw new InternalDiscourseException("Failed to test rules");
        }
        boolean alternativeReactionExists = false;
        MoreRules.Reaction bestReaction = reactions.get(0);
        for (int i = 1; i < reactions.size(); i++) {
          MoreRules.Reaction ri = reactions.get(i);
          if (!bestReaction.consumed().containsAll(ri.consumed())) {
            // Oops. Different sets of rules do different work. That's not good.
            throw new DivergentRulesScanException(clazz,
                MoreSets.difference(ri.consumed(), bestReaction.consumed()));
          }
          if (ri.consumed().size() == bestReaction.consumed().size()
              && ri.evaluated().size() == bestReaction.evaluated().size()) {
            alternativeReactionExists = true;
          }
        }
        if (!bestReaction.consumed().containsAll(allPropertyNames)) {
          // Oops. Not all properties are consumed. That's not good.
          throw new InsufficientRulesScanException(clazz, allPropertyNames,
              MoreSets.difference(allPropertyNames, bestReaction.consumed()));
        }
        if (alternativeReactionExists) {
          // Oops. There are multiple ways to evaluate and construct the command. That's not good.
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "There is no best way to evaluate and construct the command, so process may not be deterministic.");
          }
        }

        // What about required properties?
        Set<String> guaranteedPropertyNames = new HashSet<>();
        for (LeafCommandProperty property : properties)
          if (property.isGuaranted())
            guaranteedPropertyNames.add(property.getName());

        // If we have just the required syntax, then can we create everything?
        List<MoreRules.Reaction> guaranteedReactions =
            MoreRules.react(rules, guaranteedPropertyNames).stream()
                .sorted(Comparator.<MoreRules.Reaction>comparingInt(ri -> -ri.consumed().size())
                    .thenComparingInt(ri -> ri.evaluated().size()))
                .toList();
        if (guaranteedReactions.isEmpty()) {
          // Is this even possible?
          throw new InternalDiscourseException("Failed to test guaranteed rules");
        }
        MoreRules.Reaction bestGuaranteedReaction = guaranteedReactions.get(0);

        // TODO instance constant
        // Set<String> requiredPropertyNames = new HashSet<>(guaranteedPropertyNames);
        // requiredPropertyNames.add("");

        // If we have just the required syntax, then can we create all the required fields?
        if (!bestGuaranteedReaction.consumed().containsAll(guaranteedPropertyNames)) {
          // Oops. Not all required properties are consumed. That's not good.
          throw new InsufficientRulesScanException(clazz, guaranteedPropertyNames,
              MoreSets.difference(guaranteedPropertyNames, bestGuaranteedReaction.consumed()));
        }
        if (!bestGuaranteedReaction.produced().contains("")) {
          // Welp, we didn't create our instance. That's bad.
          // TODO We could be clearer in how we "phrase" <instance> here
          throw new InsufficientRulesScanException(clazz, guaranteedPropertyNames,
              Set.of("<instance>"));
        }

        // What about all the other property names? If we have the guaranteed property names PLUS
        // one other, then can we assign the other?
        for (String propertyName : allPropertyNames) {
          if (guaranteedPropertyNames.contains(propertyName))
            continue;

          Set<String> availableNames = new HashSet<>(guaranteedPropertyNames);
          availableNames.add(propertyName);

          MoreRules.Reaction reaction = MoreRules.react(rules, availableNames).stream()
              .sorted(Comparator.<MoreRules.Reaction>comparingInt(ri -> -ri.consumed().size())
                  .thenComparingInt(ri -> ri.evaluated().size()))
              .findFirst().orElseThrow(() -> new AssertionError("Failed to test guaranteed rules"));

          if (!reaction.consumed().containsAll(availableNames)) {
            // Oops. Not all required properties are consumed. That's not good.
            throw new InsufficientRulesScanException(clazz, availableNames,
                MoreSets.difference(availableNames, reaction.consumed()));
          }
          if (!reaction.produced().contains("")) {
            // Welp, we didn't create our instance. That's bad.
            throw new InsufficientRulesScanException(clazz, availableNames, Set.of("<instance>"));
          }
        }

        // We have a clear winner for evaluation and construction. Let's build our body.
        body = new CommandBody(properties, bestReaction.evaluated());
      }

      preparedClasses.add(new PreparedClass(walkedClass.supercommand(), walkedClass.clazz(),
          walkedClass.configurable(), Optional.ofNullable(body)));
    }

    // Make sure our prepared classes have unique coordinates
    for (PreparedClass<? extends T> preparedClass : preparedClasses) {
      // Do we have any duplicate usage of coordinates?
      preparedClass.body().stream().flatMap(b -> b.getProperties().stream())
          .flatMap(p -> p.getCoordinates().stream()).collect(duplicates())
          .ifPresent(duplicateCoordinates -> {
            throw new DuplicateCoordinatesScanException(preparedClass.clazz(),
                duplicateCoordinates);
          });

      // Do we have any duplicate usage of property names?
      preparedClass.body().stream().flatMap(b -> b.getProperties().stream())
          .map(LeafCommandProperty::getName).collect(duplicates()).ifPresent(duplicateNames -> {
            throw new DuplicatePropertyNamesScanException(preparedClass.clazz(), duplicateNames);
          });
    }

    return preparedClasses;
  }

  private <T> RootCommand<T> doTreeStep(List<PreparedClass<? extends T>> preparedClasses,
      InvocationContext context) {
    RuleEvaluator evaluator = context.get(RULE_EVALUATOR_KEY).orElseThrow();

    RulesEngine reactor = new RulesEngine(evaluator);

    RootCommand<T> root;
    try {
      getListener(context).beforeScanStepTree(preparedClasses, context);
      root = treeStep(reactor, preparedClasses, context);
      getListener(context).afterScanStepTree(preparedClasses, root, context);
    } catch (Throwable t) {
      getListener(context).catchScanStepTree(t, context);
      throw t;
    } finally {
      getListener(context).finallyScanStepTree(context);
    }

    return root;
  }

  protected <T> RootCommand<T> treeStep(RulesEngine reactor,
      List<PreparedClass<? extends T>> preparedClasses, InvocationContext context) {
    // We need to visit our classes in dependency order. That is, we want to process a node only
    // after all of its dependencies have been processed. (So, leaves first, root last.) We will
    // need a dependency graph to compute that, so let's build it here.
    Map<Class<?>, Set<Class<?>>> shallowDependencies = new HashMap<>();
    for (PreparedClass<? extends T> bodiedClass : preparedClasses) {
      shallowDependencies.put(bodiedClass.clazz(), new HashSet<>());
    }
    for (PreparedClass<? extends T> bodiedClass : preparedClasses) {
      if (bodiedClass.supercommand().isPresent()) {
        SuperCommand<?> supercommand = bodiedClass.supercommand().get();
        shallowDependencies.get(supercommand.clazz()).add(bodiedClass.clazz());
      }
    }

    // Now compute the topological ordering of our graph. In other words, we want to sort our
    // classes in dependency order. We want to process a node only after all of its dependencies
    // have been processed. So leaves first, root last.
    List<Class<?>> sortedClasses = new ArrayList<>(shallowDependencies.keySet());
    sortedClasses.sort(Graphs.topologicalOrdering(shallowDependencies));


    // Let's gather our discriminators
    Map<Class<?>, Discriminator> discriminators = new HashMap<>();
    for (PreparedClass<? extends T> bodiedClass : preparedClasses) {
      if (bodiedClass.supercommand().isPresent()) {
        discriminators.put(bodiedClass.clazz(), bodiedClass.supercommand().get().discriminator());
      }
    }

    // Now let's create our nodes in the order we just sorted them. This will ensure that we
    // process a node only after all of its dependencies have been processed.
    List<RootCommand<?>> roots = new ArrayList<>();
    Map<Class<?>, Command<?>> subcommands = new HashMap<>();
    for (PreparedClass<? extends T> preparedClass : preparedClasses) {
      Class<?> clazz = preparedClass.clazz();

      Set<Class<?>> deps = shallowDependencies.get(clazz);
      if (deps == null) {
        throw new AssertionError("no dependencies for " + clazz);
      }

      Map<Discriminator, Command<?>> subs = new HashMap<>();
      for (Class<?> dep : deps) {
        Command<?> sub = subcommands.get(dep);
        if (sub == null) {
          throw new AssertionError("no subcommand for " + dep);
        }

        Discriminator discriminator = discriminators.get(dep);
        if (discriminator == null) {
          throw new AssertionError("no discriminator for " + dep);
        }

        subs.put(discriminator, sub);
      }

      String description;
      DiscourseDescription descriptionAnnotation =
          preparedClass.clazz().getAnnotation(DiscourseDescription.class);
      if (descriptionAnnotation != null) {
        description = descriptionAnnotation.value();
      } else {
        description = null;
      }

      // TODO should command be generic?
      Command<?> command;
      if (subs.isEmpty()) {
        CommandBody body = preparedClass.body().orElseThrow(() -> {
          throw new LeafCommandMissingBodyScanException(preparedClass.clazz());
        });
        // We want the leaf command to be immutable by default. If anyone wants to make it mutable
        // down the line, they can always make a copy.
        List<LeafCommandProperty> immutablePropertiesCopy = List.copyOf(body.getProperties());
        command = new LeafCommand<>(description, immutablePropertiesCopy,
            toReactor(reactor, body.getRules()), toConstructor(preparedClass.clazz()));
      } else {
        command = new com.sigpwned.discourse.core.command.tree.SuperCommand(description, subs);
      }

      if (preparedClass.supercommand().isPresent()) {
        // This is a subcommand
        Discriminator discriminator = discriminators.get(clazz);
        if (discriminator == null) {
          throw new AssertionError("no discriminator for " + clazz);
        }

        subcommands.put(clazz, command);
      } else {
        // This is a root command
        roots.add(new RootCommand(preparedClass.configurable().name(),
            preparedClass.configurable().version(), command));
      }
    }

    if (roots.size() != 1) {
      throw new AssertionError("expected exactly one root command, got " + roots);
    }

    return (RootCommand<T>) roots.get(0);
  }

  private Consumer<Map<String, Object>> toReactor(RulesEngine reactor, List<NamedRule> rules) {
    return arguments -> {
      // TODO instance constant
      Map<String, Object> reacted = reactor.run(arguments, rules);
      arguments.clear();
      arguments.putAll(reacted);
    };
  }

  private <T> Function<Map<String, Object>, T> toConstructor(Class<T> clazz) {
    return arguments -> {
      // TODO instance constant
      Object instance = arguments.get("");
      return clazz.cast(instance);
    };
  }
}
