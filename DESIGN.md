# DESIGN

## Goals

The library should provide the following core functionality:

* Parse command-line arguments into a user-friendly format
* Define user-friendly formats using annotations on Java classes
* Define user-friendly formats programatically

The library should also be:

* Customizable -- Modify existing functionality
* Extensible -- Add new functionality
* Modular -- It should be easy to package the above in easy-to-install modules, and these modules should coexist to the greatest extent possible

## Design Philosophy

Given that the library must support modular extensions where multiple modules can coexist, the architecture must support plugin-like functionality. There are two main strategies for implementing plugin architectures:

1. Composition Strategies -- Program behavior is defined using a set of abstractions expressed as interfaces. The application provides default implementations for these interfaces for a robust out-of-the-box experience. Users can modify application behavior by providing custom implementations of these interfaces, and the application uses some inversion-of-control technique (direct parameterization, dependency injection, etc.) to allow the user to choose which implementations are used. This approach works well when only a few customizations are needed, and cross-customization is not required.
    * BYOC (Bring Your Own Component) -- Users provide completely novel implementations. This approach generally does not stack well with multiple customizations because only one implementation "wins."
    * Facade / Adapter -- Users provide "wrappers" for default implementations, and the wrappers can modify inputs before calling their delegate, and modify outputs after their delegate returns. This approach can stack well with multiple customizations, but application initialization typically involves the complex functional composition of multiple object factories, where order matters.
2. Pipeline Strategies -- Program behavior is defined as an ordered sequence of steps. The application provides default implementations of these steps, which are typically fixed, at least outside of testing. The framework then provides a well-defined set of "hooks," and users can provide implementations of these hooks to modify application behavior. Typical hooks are:
    * Inter-step -- The framework provides hooks between steps. Each step defines a set of inputs and outputs. The framework provides "before" and "after" hooks that allow users to view and modify step inputs and outputs, respectively. This approach can stack very well with multiple customizations using the Chain of Responsibility pattern.
    * Intra-step -- The framework provides hooks within steps. This is typically implemented by step implementations exposing parameters that allow users to provide custom implementations of key portions of step behavior via inversion-of-control. This approach can stack very well with multiple customizations using the Chain of Responsibility pattern.
  
Given the importance of the modularity requirement, I elected to use the pipeline strategy for this implementation with both inter- and intra-step hooks. Hooks are generally provided to the framework as elements of a Chain of Responsibility, where users can register their implementations as the first or last link in the chain. This allows logical groups of features to be provided by a common container object while also providing flexibility for processing order of customizations when needed. This approach to framework design makes customization a fundamental aspect of the design.

## Pipeline

The pipeline is complex. We will explore its development below in a stepwise fashion to motivate why each step exists.

### Basic Command-Line Parsing

Let's imagine we want to support only basic command-line syntax:

* Options -- Switches (e.g., `--alpha`) followed by a literal string value
* Positional arguments -- A literal string value at a fixed logical position in the command line

For example, the following command line arguments:

    --alpha 1 --bravo 2 hello world
    
Which Java would provide to the application as the following string list:

    [ "--alpha", "1", "--bravo", "2", "hello", "world" ]
    
Which the framework should parse as:

    --alpha # switch
    1       # option "alpha" value
    --bravo # switch
    2       # option "bravo" value
    hello   # positional parameter
    world   # positional parameter
    
To be sure, discourse supports substantially more complex syntax than the above. However, for the purpose of designing out this first step in the pipeline, imagine only this syntax is supported for now.

The following steps would support the parsing nicely:

* tokenize (`List<String> args` → `List<Token>`) -- Maps each element of `args` to a `Token`, which represents either a switch or a value. In this simple language, the implementation would only need to check if the element starts with `"-"` to determine the `Token` type.
* parse (`List<Token> tokens` → `List<Map.Entry<Object, String>>`) -- Parses the list of `Token` into a list of key-value pairs, where the key is the argument's logical "coordinate" and the value is the argument's natural `String` value. The coordinate for an option parameter would be the name of the switch (e.g., `alpha`, `bravo`). The coordinate for a positional parameter would be the `Integer` position of the parameter (e.g., `0`, `1`). A `List<Map.Entry<Object,String>>` is used instead of a `Map<Object,String>` because (a) order matters, and (b) the same map key coordinate may appear multiple times, for example, if the same option is given multiple times.
* gather (`List<Map.Entry<Object, String>> parsedArgs` → `Map<Object,List<String>>`) -- Transforms the `parsedArgs` list of key-value arguments into a map from key to list of value. Within each map key coordinate, the order of values is preserved. The keys in the map are ordered according to the first appearance of the key in the arguments. This transformation is transparent.

The above example:

    args: [ "--alpha", "1", "--bravo", "2", "hello", "world" ]
    
Would execute as:

1. tokenize(args) → `[ { "type": "switch", "value": "alpha" }, { "type": "value", "value": "1" }, { "type": "switch", "value": "bravo" }, { "type": "value", "value": "2" }, { "type": "value", "value": "hello" }, { "type": "value", "value": "world" } ]`
2. parse(#1) → `[ { "alpha": "1" }, { "bravo": "2" }, { 0: "hello" }, { 1: "world" } ]`
3. gather(#2) → `{ "alpha": [ "1" ], "bravo": [ "2" ], 0: [ "hello" ], 1: [ "world" ] }`

This pipeline parses and organizes command line arguments into a user-friendly `Map` indexed by logical argument coordinate.

### Named Parameters

The above pipeline is already interesting and usable. However, it requires users to know the logical coordinate of the data they need as opposed to something more user-friendly, like an attribute name. The following new step introduces a new input from the application developer, `parameterNames`, and a new step to make use of it:

* attribute (`Map<Object, String> parameterNames`, `List<Map.Entry<Object, String>> parsedArgs` → `List<Map.Entry<String, String>>`) -- Uses the given `parameterNames` to rewrite the key-value pairs from `parsedArgs` to the result. The result is a list of key-value mappings from parameter name to value. This runs between the `parse` and `gather` steps. This transformation is transparent.

The example:

    args:           [ "--alpha", "1", "--bravo", "2", "hello", "world" ]
    parameterNames: { "alpha": "alpha", "bravo": "bravo", 0: "greeting", 1: "entity" }
    
Would execute as:

1. tokenize(args) → `[ { "type": "switch", "value": "alpha" }, { "type": "value", "value": "1" }, { "type": "switch", "value": "bravo" }, { "type": "value", "value": "2" }, { "type": "value", "value": "hello" }, { "type": "value", "value": "world" } ]`
2. parse(#1) → `[ { "alpha": "1" }, { "bravo": "2" }, { 0: "hello" }, { 1: "world" } ]`
3. attribute(parameterNames, #2) → `[ { "alpha": "1" }, { "bravo": "2" }, { "greeting": "hello" }, { "entity": "world" } ]`
4. gather(#3) → `{ "alpha": [ "1" ], "bravo": [ "2" ], "greeting": [ "hello" ], "entity": [ "world" ] }`

The pipeline now parses and organizes command line arguments into a user-friendly `Map` indexed by logical property name.

### Parameter Deserialization

The above pipeline still requires users to perform any required translation from strings on their own. The following new steps bake in a mechanism for converting strings into domain objects inline:

* map (`Map<String, Function<String, Object>> mappers`, `Map<String, List<String>> gatheredArgs` → `Map<String, List<Object>>`) -- Uses the given `mappers` to rewrite the entries from `gatheredArgs` to the result. The `mappers` parameter provides a deserializer for at least each named argument in `gatheredArgs`. The result is a map from parameter name to the list of domain values, as mapped by the entries in `mappers`.
* reduce (`Map<String, Function<List<Object>, Object>> reducers`, `Map<String, List<Object>> mappedArgs` → `Map<String, Object>`) -- Uses the given `reducers` to rewrite the entries from `mappedArgs` to the result. The `reducers` parameter provides a reducer for at least each named argument in `mappedArgs`. These functions allow the pipeline to perform aggregate transformations on the list of values given by the user to prepare them for the application's use, for example turning the list into a set, or taking the first element of the list. The result is a map from parameter name to the single domain value, as reduced by the entries in `reducers`.

The example:

    args:           [ "--alpha", "1", "--bravo", "2", "hello", "world" ]
    parameterNames: { "alpha": "alpha", "bravo": "bravo", 0: "greeting", 1: "entity" }
    mappers:        { "alpha": Integer::parseInt, "bravo": Long::parseLong, "greeting": String::valueOf, "entity": String::valueOf }
    reducers:       { "alpha": xs -> List::copyOf, "bravo": xs -> Set::copyOf, "greeting": xs -> xs.get(0), "entity": xs -> xs.get(0) }
        
Would execute as:

1. tokenize(args) → `[ { "type": "switch", "value": "alpha" }, { "type": "value", "value": "1" }, { "type": "switch", "value": "bravo" }, { "type": "value", "value": "2" }, { "type": "value", "value": "hello" }, { "type": "value", "value": "world" } ]`
2. parse(#1) → `[ { "alpha": "1" }, { "bravo": "2" }, { 0: "hello" }, { 1: "world" } ]`
3. attribute(parameterNames, #2) → `[ { "alpha": "1" }, { "bravo": "2" }, { "greeting": "hello" }, { "entity": "world" } ]`
4. gather(#3) → `{ "alpha": [ "1" ], "bravo": [ "2" ], "greeting": [ "hello" ], "entity": [ "world" ] }`
5. map(mappers, #4) → `{ "alpha": [ 1 ], "bravo": [ 2L ], "greeting": [ "hello" ], "entity": [ "world" ] }`
6. reduce(reducers, #5) → `{ "alpha": List.of(1), "bravo": Set.of(2L), "greeting": "hello", "entity": "world" }`

The pipeline now parses and organizes command line arguments into a user-friendly `Map` of transformed domain objects indexed by property name.

### Model Object Creation

The above pipeline requires users to work with a raw, untyped map of attributes by name. The following new step maps this map to a statically-typed model object ready for use by the application.

* finish (`Function<Map<String, T>, Object> finisher`, `Map<String, Object> reducedArgs` → `T`) -- Uses the given function to interpret the map and return a model object.

The example:

    // Of course, this could be model object, or Runnable application object, or whatever you like.
    class Example {
        public List<Integer> alpha;
        public Set<Long> bravo;
        public String greeting;
        public String entity;
        
        public static Example fromReducedArgs(Map<String, Object> reducedArgs) {
            Example result=new Example();
            result.alpha = (List<Integer>) reducedArgs.get("alpha");
            result.bravo = (Set<Long>) reduceArgs.get("bravo");
            result.greeting = (String) reducedArgs.get("greeting");
            result.entity = (String) reducedArgs.get("entity");
            return result;
        }
    }
    
    args:           [ "--alpha", "1", "--bravo", "2", "hello", "world" ]
    parameterNames: { "alpha": "alpha", "bravo": "bravo", 0: "greeting", 1: "entity" }
    mappers:        { "alpha": Integer::parseInt, "bravo": Long::parseLong, "greeting": String::valueOf, "entity": String::valueOf }
    reducers:       { "alpha": xs -> List::copyOf, "bravo": xs -> Set::copyOf, "greeting": xs -> xs.get(0), "entity": xs -> xs.get(0) }
    finisher:       Example::fromReducedArgs
        
Would execute as:

1. tokenize(args) → `[ { "type": "switch", "value": "alpha" }, { "type": "value", "value": "1" }, { "type": "switch", "value": "bravo" }, { "type": "value", "value": "2" }, { "type": "value", "value": "hello" }, { "type": "value", "value": "world" } ]`
2. parse(#1) → `[ { "alpha": "1" }, { "bravo": "2" }, { 0: "hello" }, { 1: "world" } ]`
3. attribute(parameterNames, #2) → `[ { "alpha": "1" }, { "bravo": "2" }, { "greeting": "hello" }, { "entity": "world" } ]`
4. gather(#3) → `{ "alpha": [ "1" ], "bravo": [ "2" ], "greeting": [ "hello" ], "entity": [ "world" ] }`
5. map(mappers, #4) → `{ "alpha": [ 1 ], "bravo": [ 2L ], "greeting": [ "hello" ], "entity": [ "world" ] }`
6. reduce(reducers, #5) → `{ "alpha": List.of(1), "bravo": Set.of(2L), "greeting": "hello", "entity": "world" }`
7. finish(finisher, #6) → `new Example(alpha=List.of(1), bravo=Set.of(2L), greeting="hello", entity="world")`

The pipeline now parses and organizes command line arguments into a user-friendly domain model object.

### Configurable Class Scanning

The above pipeline requires the user to provide a lot of metadata. The following new step allows the user to capture this information using annotations on the model object itself.

* scan (`Class<T> clazz` → `Command<T> command`) -- Analyzes the given `clazz` using the reflection API to discover the parameters defined by the class using annotations. The resulting `Command` object stores data from which `parameterNames`, `mappers`, `reducers`, and `finishers` may be derived.
* plan (`Command<T> command` → `PlannedCommand<T> plannedCommand`) -- Analyzes the given `command` and derives the values for `parameterNames`, `mappers`, `reducers`, and `finishers`. The resulting `PlannedCommand` object contains the values for all of the above.

The `scan` and `plan` steps are separate to allow users to use an explicit `Command` object created from scratch instead of one generated by using `scan`.

The example:

    // Of course, this could be model object, or Runnable application object, or whatever you like.
    @Configurable
    class Example {
        @OptionParameter(longName="alpha")
        public List<Integer> alpha;
        
        @OptionParameter(longName="bravo")
        public Set<Long> bravo;
        
        @PositionalParameter(position=0)
        public String greeting;
        
        @PositionalParameter(position=1)
        public String entity;
    }
    
    args:           [ "--alpha", "1", "--bravo", "2", "hello", "world" ]
    clazz:          Example.class
        
Would execute as:

1. scan(clazz) → `Command(...)`
2. plan(#1) → `PlannedCommand(parameterNames, mappers, reducers, finisher)`
3. tokenize(args) → `[ { "type": "switch", "value": "alpha" }, { "type": "value", "value": "1" }, { "type": "switch", "value": "bravo" }, { "type": "value", "value": "2" }, { "type": "value", "value": "hello" }, { "type": "value", "value": "world" } ]`
4. parse(#3) → `[ { "alpha": "1" }, { "bravo": "2" }, { 0: "hello" }, { 1: "world" } ]`
5. attribute(#2.parameterNames, #4) → `[ { "alpha": "1" }, { "bravo": "2" }, { "greeting": "hello" }, { "entity": "world" } ]`
6. gather(#5) → `{ "alpha": [ "1" ], "bravo": [ "2" ], "greeting": [ "hello" ], "entity": [ "world" ] }`
7. map(#2.mappers, #6) → `{ "alpha": [ 1 ], "bravo": [ 2L ], "greeting": [ "hello" ], "entity": [ "world" ] }`
8. reduce(#2.reducers, #7) → `{ "alpha": List.of(1), "bravo": Set.of(2L), "greeting": "hello", "entity": "world" }`
9. finish(#2.finisher, #8) → `new Example(alpha=List.of(1), bravo=Set.of(2L), greeting="hello", entity="world")`

Note that the explicit `fromReducedArgs` method is no longer required. The `scan` and `plan` steps automatically discover the structure of the `Example` class and provide a default `finisher` value.

The pipeline now parses and organizes command line arguments into a user-friendly domain model object using only an annotated class as input.

### Additional Steps

The above pipeline already provides a simple, robust approach to defining and parsing command-line options. However, there are a few more steps added for quality of life, extensibility, and feature completeness:

* resolve (`List<String> args` → `ResolvedCommand<T>`) -- Many command-line applications define multiple "modes," such as `git` (e.g., `git push`, `git clone`, `git pull`, etc.). This step allows the application developer to choose which `Command` to run based on user input. The final scan step now produces a `RootCommand<T>` which contains multiple possible commands encoded in a class hierarchy as a result, and the default implementation of the resolver uses that value to perform the resolution.
* preprocessCoordinates (`Map<Coordinate, String> parameterNames` → `Map<Coordinate, String>`) -- This step allows application developers to customize the attributes the application defines and the coordinates used to collect them. This allows module developers to inject custom fields on the fly, for example, to print a help message and exit. Also, note that coordinates are also explicit `Coordinate` model objects now instead of bare Java objects for module developer QOL.
* preprocessArgs (`List<String> args` → `List<String>`) -- The primary function of this library is to process command-line arguments. This hook allows module developers to make changes to command line arguments as needed.
* preprocessTokens (`List<Token> tokens` → `List<Token>`) -- This hook is designed to allow module developers to support novel command-line syntax.

The final pipeline definition is:

1. scan(clazz)
2. resolve(#1)
3. plan(#2)
4. preprocessCoordinates(#3.coordinates)
5. preprocessArgs(args)
6. tokenize(#5)
7. preprocessTokens(#6)
8. parse(#7)
9. attribute(#4, #8)
10. gather(#9)
11. map(#3.mappers, #10)
12. reduce(#3.reducers, #11)
13. finish(#3.finisher, #12)

### Other Pipeline Considerations

#### Why Not Split scan Step?

Without a doubt, the scan step is the most complex. It has by far the most parameters for inversion-of-control: SubClassScanner, NamingScheme, SyntaxNominator, SyntaxDetector, SyntaxNamer, RuleNominator, RuleDetector, and RuleNamer. (In fact, scan has 8 of 10 total Chains of Responsibility!) So why not split it up?

* From a developer experience perspective, you're either scanning a class or bringing your own. There is no in-between. So keeping scan self-contained makes sense from this perspective.
* Changing any of these parameters only makes sense in the context of the scan step as a whole.
* There is no useful intermediate result within the scan step, so there is no crisp interface to define otherwise.

#### Then Why Split the Other Steps Up?

The other steps have crisp interfaces and more than one reason for customization. For example, one might use preprocessCoordinates to add custom coordinates for help flags, or to add novel syntax with custom coordinates.

## Abstractions

These concepts are broken out very carefully to minimize coupling and maximize orthogonality. For example the `SyntaxNominator` is separated from the `SyntaxDetector` because new `SyntaxNominator` implementations are added as Java adds new syntax elements, but new `SyntaxDetector` implementations are added as module developers define novel syntax constructs for the command line. Similarly, `RuleDetector` doesn't just name the rules because the `SyntaxNominator` is in charge of naming things, and a new rule type should take advantage of whatever naming strategies are currently registered.

### BYOC Constructs

* Syntax -- Defines how CLI syntax is "spelled" on the command line (e.g., Unix-style `-x` vs. Windows-style `/x`)

### Chain of Responsibility Constructs

* ValueDeserializerFactory<T> -- Generates `mapper` functions 
* ValueSink -- Generates `reduce` functions
* SubClassScanner -- Given a `@Configurable` class, identifies any subclasses. Not hard-coded to allow the framework to take advantage of future Java features, e.g., sealed classes
* NamingScheme -- Generates parameter names from annotated fields, getters, setters, etc.
* SyntaxNominator -- Lists all syntactical elements of a Java class that might define a parameter
* SyntaxDetector -- Identifies the nominated syntactical elements that do define a parameter
* SyntaxNamer -- Given a parameter-bearing syntactical element and a NamingScheme, name the element
* RuleNominator -- Lists all syntactical elements of a Java class that might be needed to construct the final model object
* RuleDetector -- Identifies which nominated syntactical elements are needed to construct the final model object
* RuleNamer -- Given a rule and a NamingScheme, name the rule

## Other Considerations

### Dependency Resolution

One of the primary challenges of plugin architecture design is data sharing. How does one enable two modules, where either may or may not have been designed with the other in mind, to share code and data without coupling them to each other? This implementation's approach is to combine IoC with a large number of software interfaces, plus a context object to allow modules to fetch dependencies on the fly.


## On Architectural Style

At the end of the day, with software architecture as with all things, the perfect is generally the enemy of the good. If the architect is faced with multiple design decisions, all of which will work, with no clear reason to pick one over the others, then the only choice is to pick one and move forward. Decisions can always be revisited later. There is, and may always be, a subjective component to software architecture, which boils down to personal style.
