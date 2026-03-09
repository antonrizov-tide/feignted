# Feign-ted: The Danger of mixing @Configuration with FeignClients

Mixing Spring’s `@Configuration` with the `@FeignClient` configuration
attribute is a recipe for silent, global side effects. We recently
encountered a production incident where a configuration intended for a
single client "leaked," overriding the behavior of every other client
in the system.

This repository provides a minimal example of the problem we encountered.

## The Problem

The project contains two clients:
1.  **Client A**: Uses `AFeignConfig` (Annotated with `@Configuration`).
2.  **Client B**: Uses `BFeignConfig` (Plain class, no annotation).

Because `AFeignConfig` is picked up by the main `@ComponentScan`, its
`ErrorDecoder` bean (which is configured to throw `AException`) is
registered in the parent context. When Client B encounters an error,
it looks for an `ErrorDecoder`. It finds the one from the parent
context and uses it, throwing `AException` instead of the expected
`BException`.

## The Role of Feign.Builder

In this example, both configurations define a `Feign.Builder`
bean. While the builder is used to construct the client, the **leak**
specifically occurs because the `ErrorDecoder` bean inside the
`@Configuration` class is promoted to the parent context.

Even if you use a custom builder, Spring’s auto-configuration for
Feign will still attempt to wire in components (like `ErrorDecoder`)
from the available context. If one is found in the parent context, it
often takes precedence over the client's own intended defaults.

## Project Structure

* `co.tide.A`: Feign Client A.
* `co.tide.AFeignConfig`: **The Problematic Config.** Annotated with `@Configuration`.
* `co.tide.B`: Feign Client B.
* `co.tide.BFeignConfig`: The "Correct" Config. No annotation.
* `co.tide.SpringMagicTest`: The integration test proving the leakage.

## How to Reproduce the Failure

1.  Clone the repository.
2.  Run the tests: `./gradlew test` (or via your IDE).
3.  Observe that `SpringMagicTest` **fails**. 
    * **Expected exception:** `co.tide.BException`
    * **Actual exception:** `co.tide.AException`

## How to Fix It

The most straight forward fix is to remove `@Configuration` annotation from
`AFeignConfig.java`:

```java
// @Configuration <--- Remove this!
public class AFeignConfig { ... }
```

To see the solution in practice, you can check out the `ok` branch,
where removing the `@Configuration` annotation ensures that
[AFeignConfig](https://github.com/antonrizov-tide/feignted/blob/ok/src/main/java/co/tide/AFeignConfig.java)
remains isolated and the tests pass as expected.

## The "I Already Knew That" Paradox

When I shared this bug with the team, the most common response
was: "Oh yeah, I knew you shouldn't do that." Yet, the offending code
had been reviewed and touched by multiple engineers, myself
included, without anyone raising a red flag. This highlights a
dangerous gap between knowing a rule exists and recognizing its
violation in a complex codebase.

Because @Configuration is the "standard" way to define beans in
Spring, our muscle memory often takes over. We see a config class, we
add the annotation. It looks "correct" to the naked eye, even though
it creates a silent, global side effect that only reveals itself when
a different client starts behaving strangely.

## Conclusion

Context leakage in Spring Cloud OpenFeign is a silent killer. It
doesn't usually crash your application on startup; instead, it subtly
alters the behavior of unrelated components, making it incredibly
difficult to debug once it reaches production.

The core of the issue is that Spring’s powerful component scanning is
too helpful, too magical, it finds your "private" Feign configurations
and promotes them to the global parent context unless you explicitly
tell it not to. By the time your integration tests fail (if you have
them), the root cause is often buried under layers of
auto-configuration magic.

Avoid annotating Feign client configuration classes with `@Configuration`.
