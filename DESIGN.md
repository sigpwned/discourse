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