# J Nano Server

## Summary

A simple, experimental HTTP 1.1 server written in Java

> [!CAUTION]
> **This is not production ready**

## Requirements

- JDK / Java Version >= 21

Reasons for Java 21 as minimum version:

- pattern matching
- virtual threads
- sealed classes
- records

## Running

- The provided example server can be gun via `./gradlew run` from the root of the repository.

## Description

This project was a proof of concept to see how difficult it would be to write a basic HTTP server from scratch in Java, utilising Virtual Threads for Parallelism & Concurrency.

I learnt a lot working on this and gained an appreciation for modern Java features.
