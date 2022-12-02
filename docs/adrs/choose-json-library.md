# Scala json library choice

* Status: accepted
* Deciders:
  * Jonathan Winandy
  * Bastien Guihard
  * Dylan Do Amaral

Technical Story: We need to serialize and deserialize Notion domain class.

## Context and Problem Statement

What JSON library should we use ?

## Decision Drivers

* Performance
* Simplicity
* Potential
* Stability
* Ecosystem
* Capabilities

## Considered Options

* [Json4s](https://github.com/json4s/json4s)
* [Circe](https://github.com/circe/circe)
* [ZIO Json](https://github.com/zio/zio-json)

## Decision Outcome

Chosen option: "Circe", because it is a library :
* with a huge community 
* used massively for years 
* agnostic 

### Positive Consequences

* It answers all our problems
* It is easy to write our own encoders using Magnolia

### Negative Consequences

* The compile time for the auto derivation is huge due to Shapeless

## Pros and Cons of the other Options

### Json4s

/!\ We didn't test a lot this solution to be honest

* Good, because it is a well maintained library with more than 1K stars
* Good, because it is totally agnostic
* Bad, because it lacks control over the generated json (specific decorator or configuration are missing)

### ZIO Json

* Good, because it belongs to the zio ecosystem (and it is ZIO Notion after all)
* Good, because it is the fastest solution (at compile time AND at run time)
* Bad, because it is hard to write our own encoder due to the optimizations
* Bad, because we can have a common configuration, and we have to add decorators everywhere
* Bad, because it is still a young library
* Bad, because we can't use it with generics (even if we don't need that YET)

## Notes

* Due to performance, we may want to use ZIO Json in the future. However, at the moment, we think that it is not a 
priority.
