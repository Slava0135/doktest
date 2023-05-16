# Doktest Plugin for Gradle

The plugin that verifies your Kotlin code inside KDoc comments.

[**Also check out Intellij IDEA plugin!**](https://plugins.jetbrains.com/plugin/21791-doktest)

## About

Sometimes you want to add samples into documention for your code.

KDoc allows you to use special `@sample` tag that inserts linked code into comment.

However there are few problems with this approach:

- There is a [bug](https://youtrack.jetbrains.com/issue/KTIJ-8414) in Intellij IDEA because of which you can't see samples in documentation for libraries (even for standard one!)
- Samples are always put in the end of comment, limiting your freedom in expressing yourself

Other approach would be using "code blocks" markdown syntax like this:

```kotlin
/**
 * ```
 * your sample goes here
 * ```
 */
fun foo() { ...
```

But there is no way to verify those samples are correct... unless you use this plugin!

Basically, this plugin allows you to write documentation tests (or doctests for short), similar to [Rust](https://doc.rust-lang.org/rustdoc/write-documentation/documentation-tests.html) or [Python](https://docs.python.org/3/library/doctest.html).

## Setup

```kotlin
plugins {
    id("io.github.slava0135.doktest") version "0.1.1"
}
...
dependencies {
    testImplementation(kotlin("test"))
}
```

You would also need a testing framework (like JUnit).

## Syntax

To write a doctest do this:

```kotlin
package foo
...
/**
 * ```kotlin doctest
 * import bar.*
 * val s = "faz"
 * assertEquals(s, faz())
 * ```
 */
fun faz() = "faz"
```

And it will be treated as following code:

```kotlin
import kotlin.test.*
import foo.*
import bar.*

fun main() {
    val s = "faz"
    assertEquals(s, faz())
}
```

You can use `import` declarations inside samples as normal and they will be inserted in import section as shown.

**NOTE**: only official KDoc style is supported, e.g. all `*` symbols should start on the same column.

```kotlin
/**
  * won't work!
 */
```

There are 3 options:

- `run` => (**default**) executes sample as normal test
- `norun` => just compiles the sample
- `nomain` => the sample code won't be surrounded with "main" function (and it won't be executed, unless you write it like test).

To use them write `doctest:option`:

```kotlin
/**
 * ```kotlin doctest:nomain
 * your sample goes here
 * ```
 */
```

## Run

To verify all samples written using syntax above are correct:

```sh
./gradlew doktest
```

There are also 2 options available for command line:

- `--file` => only samples from specified file will be tested (doesn't have to be full path - just unambiguous, treated as file suffix without `.kt` extension)
- `--line` => only sample on specified line will be tested (use with `--file` option)

```sh
./gradlew doktest --file foo --line 42
```

Will test sample from `foo.kt` on line `42` (or will fail if there is no sample to be found)

## Configuration

By default it uses `JUnit Platform` testing framework for executing samples but you can change this as in any other `Test` task:

```kotlin
tasks.doktestTest {
    useJUnitPlatform() // default, you don't need to write this
}
```

## Notes

I consider this plugin to be experimental (maybe expect some issues).

Feedback and help would be appreciated!
