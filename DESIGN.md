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
    
Would parse as:

    --alpha # switch
    1       # option "alpha" value
    --bravo # switch
    2       # option "bravo" value
    hello   # positional parameter
    world   # positional parameter
    
To be sure, discourse supports substantially more complex syntax than the above. However, for the
purpose of building out this first step in the pipeline, imagine only this syntax is supported for now.
  


# DESIGN EVOLUTION

The Discourse library has undergone a number of design changes since its inception. This document
outlines the major design changes that have occurred over time.

## Version 0.x -- The `InvocationBuilder` Versions

The primary goal for the 0.x versions was to be a simple, lightweight library that could be used to
model and parse user input from command line arguments. The initial focus was on being simple and
easy to use, with a focus on providing a clean and intuitive API for defining and parsing command
line arguments with minimal boilerplate and no demands on overall application architecture.

### Single Commands and Configurable Fields

The first release had a simple model:

* The user creates a "configurable" object, which is simply a Java class with a public default
  constructor and public mutable fields to represent the structure of user input where the class is
  annotated with the `@Configurable` annotation and the fields are annotated with annotations
  like `@FlagParameter`, `@OptionParameter`, and so on to encode the syntax of user input on the
  command line.
* The user creates a `Command` object by "scanning" the configurable object, which contains a model
  of the class' fields and annotations
* The user then creates an `Invocation` from the command object and the command line arguments using
  an `InvocationBuilder`, which uses the model data in the `Command` to interpret the given user
  arguments and create and populate an instance of the configurable class with the given user input.
* The user could support custom field types by registering a `DeserializerFactory`, which would
  handle converting strings to values on the fly.

For example, the a configurable class might look like:

```java

@Configurable
public class HelloWorldConfiguration {

  @OptionParameter(shortName = "g", longName = "greeting", description = "The greeting to use")
  public String greeting = "Hello";

  @PositionalParameter(position = 0)
  public String name;
}
```

This design was clear, simple, and effective. However, it was insufficient to handle some more
complex data types.

### Sinks

The next major design change was the introduction of "sinks". Sinks are a way to handle more complex
data types that can't be easily represented by a simple field in a configurable object. For example,
a `List` of values.

In the previous design, the supported data types looked like `int`, `float`, `String`, `URL`, and so
on. If the user wanted to represent a more complex type, like a `List<String>`, then they would have
to create a custom field type and a custom deserializer to handle it. If they then wanted to support
a `List<Integer>`, they would have to create another custom field type and another custom
deserializer to handle that field, too. This was cumbersome, and it was clear that the design needed
improvement.

Therefore, the concept of "sinks" was introduced. A sink is an abstraction that represents assigning
a value to a field in a configurable object. Where in the previous design, assignment to fields was
done directly using the reflection API, now assignment is done through a sink. Users register sinks
in a [chain](https://en.wikipedia.org/wiki/Chain-of-responsibility_pattern), and the sinks are then
chosen based on the type of the field being assigned to. The sink can also postprocess the data
types it accepts, which allows for one sink implementation to support all `List` types.

```java
/**
 * <p>
 * An assignment target. A sink receives zero or more values and stores them for later retrieval.
 * The sink implements an arbitrary policy for combining multiple values into a single value.
 * </p>
 */
public interface Sink {

  /**
   * Returns {@code true} if this sink accepts multiple values (e.g., for a {@link List} or
   * {@link Set}), or {@code false} otherwise.
   */
  public boolean isCollection();

  /**
   * Returns the type of the value that this sink accepts. If this sink is a collection, this method
   * returns the type of the elements in the collection.
   *
   * @return the type of the value that this sink accepts
   */
  public Type getGenericType();

  /**
   * Writes the given value into the sink. This can perform any operation, such as overwriting the
   * current value, adding the value to a collection, etc.
   *
   * @param value the value to write
   */
  public void put(Object value);

  /**
   * Returns the value stored in the sink. If the sink is has {@link #put(Object) received} zero
   * values, this method returns an empty {@link Optional}. Otherwise, this method returns the value
   * stored in the sink.
   *
   * @return the value stored in the sink
   */
  public Optional<Object> get();
}
```

This addition allows users to customize not only what values to accept, but also how those values
are aggregated for assignment. This design change was a significant improvement over the previous
design, as it allowed for more complex data types to be supported with minimal boilerplate.

### Multiple Commands

The next major design change was the introduction of multiple commands.

In the previous design, the user could only define a single command. This was limiting, as many
applications have multiple commands or "modes" that can be run, each of which takes different
arguments. The new design allows for multiple commands to be defined, each with its own configurable
object and command line syntax.

The new design split made the `Command` class abstract and introduced two concrete
subclasses: `MultiCommand`, which represents a command "choice", and `SingleCommand`, which
represents a single command.

```java

@Configurable(
    name = "congeniality",
    description = "A command for saying hello and goodbye",
    subcommands = {
        @Subcommand(discriminator = "hello", command = HelloCommand.class),
        @Subcommand(discriminator = "goodbye", command = GoodbyeCommand.class)
    })
public abstract class RootCommand {

  @PositionalParameter(position = 0)
  public String name;
}

@Configurable(disciminator = "hello", description = "Say hello")
public class HelloCommand extends RootCommand {

  @OptionParameter(shortName = "s", longName = "salutation", description = "The salutation to use")
  public String salutation = "Hello";
}

@Configurable(disciminator = "goodbye", description = "Say goodbye")
public class GoodbyeCommand extends RootCommand {

  @OptionParameter(shortName = "v", longName = "valediction", description = "The valediction to use")
  public String valediction = "Goodbye";
}
```

In the above example, the user would scan `RootCommand`, which would produce a `MultiCommand` object
with two subcommands: `HelloCommand` and `GoodbyeCommand`. The `InvocationBuilder` would then choose
the appropriate subcommand based on the discriminator value in the command line arguments.

```bash
# Say hello to Alice
$ java -jar congeniality.jar hello Alice

# Say goodbye to Bob
$ java -jar congeniality.jar goodbye Bob
```

This design change was a significant improvement over the previous design, as it allowed for more
complex applications to be modeled and parsed with minimal boilerplate.

## Version 1.x -- The `InvocationPipeline` Versions

The primary goal for the 1.x versions was to be more flexible and extensible than the 0.x versions.
While the user could achieve limited extensibility in the 0.x versions by registering custom field
deserializers, sinks, and `InvocationBuilder` wrapper implementations, the 1.x versions aimed to
provide a more general-purpose extensibility mechanism. The chosen approach was to define a standard
"pipeline" of steps that library follows to parse user input, and then allow users to customize the
pipeline by implementing "hooks" between steps (for light customization), or by replacing entire
steps (for heavy customization).

The steps in the pipeline are as follows:

1. Scan -- As in the 0.x versions, the user scans a configurable object to produce a `RootCommand`
   object (`Class<T>` &rarr; `RootCommand<T>`)
2. Resolve -- The user provides the command line arguments and the `RootCommand` object to the
   `InvocationPipeline`, which resolves the command line arguments to a `Command` object and the
   remaining user arguments.
   (`RootCommand<T>`, `List<String>` &rarr; `Command<T>`, `List<String>`)
3. Parse -- The `InvocationPipeline` parses the remaining user arguments into a list of coordinate
   to string mappings. (`Command<T>`, `List<String>` &rarr; `List<Map.Entry<String, String>>`)
4. Eval -- The `InvocationPipeline` evaluates the coordinate to string mappings to produce a map of
   attribute names to values.
   (`Command<T>`, `List<Map.Entry<String, String>>` &rarr; `Map<String, Object>`)
5. Factory -- The `InvocationPipeline` uses the map of attribute names to values to create an
   instance of the configurable object. (`Command<T>`, `Map<String, Object>` &rarr; `T`)

### The `InvocationPipeline`

The `InvocationPipeline` is a sequence of steps that the library follows to parse user input. The
