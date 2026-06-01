# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`com.kendamasoft:dns-client` — a compact, zero-dependency DNS client library for network utilities and testing apps. Published to Maven Central. It builds and parses raw DNS wire-format messages and sends them over UDP, TCP, or DNS-over-HTTPS.

## Hard compatibility constraint

The library targets **Android 5.0+ (API 21) and JRE 8+** (`sourceCompatibility`/`targetCompatibility = 1.8` in `build.gradle`). The two axes are independent: API 21 is the supported-platform floor (chosen because that's where TLS 1.2 is enabled by default, which `DnsConnectionDoh` needs), while Java 8 is the language/bytecode level. Broad compatibility is a primary feature, not an accident.

The critical rule is **Java 8 _language features_ yes, Java 8 _runtime APIs_ no**:
- ✅ **Allowed — Java 8 syntax:** lambdas, method references, default/static interface methods, try-with-resources. D8 desugaring backports these to every Android API level, so they are safe at API 21 with no action required from consumers.
- ❌ **Avoid — Java 8 library APIs:** `java.time`, `java.util.stream`, `Optional`, `java.util.function`, `CompletableFuture`. These are native only on API 24+; on API 21–23 they force the *consuming app* to enable core library desugaring or they crash at runtime. Subtlety: a lambda *implementing* a `Comparator` is fine, but the `Comparator.comparingInt(...)` static factory is one of these APIs — avoid it. When unsure about an API, prefer the Java 7-era equivalent.
- ❌ **No third-party runtime dependency.** The only declared dependency is JUnit 4, and it is `testImplementation` only.

The existing code uses no Java 8 APIs (only `AtomicInteger`, `java.net.*`, `ArrayList`/`Collections`/`Comparator`, `HttpsURLConnection`), so the source-level bump changed nothing at runtime — it only unlocks the syntax above.

## Build & test

Use the Gradle wrapper (`./gradlew`); the project pins Gradle 8.14.5 (runs on JDK 8–24). The Java 8 target is enforced via `options.release = 8` on `JavaCompile` (in `build.gradle`), which compiles against the Java 8 API surface so accidental use of newer JDK APIs fails the build — building therefore requires JDK 9+ (CI pins Temurin 17).

```bash
./gradlew assemble        # compile + build jars (what CI runs)
./gradlew build           # assemble + run tests
./gradlew test            # run unit tests only
./gradlew clean

# Run a single test class or method:
./gradlew test --tests com.kendamasoft.dns.protocol.BufferUnitTest
./gradlew test --tests 'com.kendamasoft.dns.protocol.BufferUnitTest.testReadByte'
```

CI (`.github/workflows/build.yml`) runs `./gradlew assemble` on every push/PR — it does **not** run tests, so run them locally. Releases are tag-driven (`v*`) via `release.yml`; publishing to Maven Central is manual via the **Central Portal** (OSSRH was shut down 2025-06-30): `./gradlew publish` uploads through the OSSRH Staging API, then you finalize the deployment at central.sonatype.com. Credentials are a Central Portal **user token** in `local.properties` (`ossrh.user`/`ossrh.key`) — see the comments at the bottom of `build.gradle`.

## Architecture

Three packages under `src/main/java/com/kendamasoft/dns/`, layered transport → protocol model → record content:

### `com.kendamasoft.dns` — transport / connection layer
`DnsConnection` is an abstract base implementing the **template method** `doRequest(Message)`: it serializes the request through a `Buffer`, calls the abstract `send(byte[])` / `receive()`, parses the response back into a `Message`, and throws `IOException` on a non-zero DNS return code. Subclasses only implement the wire transport:
- `DnsConnectionUdp` — datagram socket, caps reads at `MAX_MESSAGE_LENGTH` (512 bytes).
- `DnsConnectionTcp` — stream socket with a 2-byte length prefix.
- `DnsConnectionDoh` — POSTs `application/dns-message` to an HTTPS URL (constructor rejects non-`https` URLs). Pass the full endpoint, e.g. `new DnsConnectionDoh("https://1.1.1.1/dns-query")`.
- `DnsConnectionAuto` — **overrides `doRequest`** to try UDP first and fall back to TCP when the response has `FLAG_TRUNCATION`; its `send`/`receive` are intentional no-ops because it delegates to fresh UDP/TCP instances.

Defaults baked into `DnsConnection`: server is hardcoded to Google `8.8.8.8` (`googleDnsAddress`) when the no-arg constructor is used, port 53, 5s socket timeout. UDP/TCP also accept an `InetAddress` to target a different resolver.

### `com.kendamasoft.dns.protocol` — message model + wire codec
- `Buffer` is the **only** class that knows the DNS byte layout. It reads/writes the header, question, and resource records, and is the single place that implements **DNS name compression** (the 0xC0 pointer scheme) in `readString()`/`readStringCompressed()`. It is fixed-size (512 bytes for writes) and stateful via a `mark` cursor. Treat it as internal.
- `Message` = header + question + three record lists (answer / authority / additional). Getters return unmodifiable (or empty) lists; `getAllRecords()` merges all three sorted by numeric type id.
- `MessageBuilder` is the public entry point for constructing requests (sets recursion-desired flag, one question, auto-incrementing transaction id).
- `Header` holds the 6 header shorts plus `FLAG_*` / `ERROR_*` bit constants and accessor helpers (`hasFlag`, `returnCode`, `opCode`).
- `RecordType` is the central enum mapping numeric type id ↔ a record content class (see below). `QuestionEntry` is package-private.

### `com.kendamasoft.dns.records` — record content (RDATA) types
Each record type parses its own RDATA. `AbstractRecord.parseData(short dataLength, Buffer)` is the contract; concrete classes (`ARecord`, `AAAARecord`, `CNAMERecord`, `MXRecord`, `NSRecord`, `PTRRecord`, `SOARecord`, `SPFRecord`, `TXTRecord`, `CAARecord`) read fields off the `Buffer` and implement `toString()` for `dig`-style output. `UnknownRecord` is the fallback that just captures raw bytes.

### How a type id becomes a parsed record
`ResourceRecord.readRecord()` looks up the `RecordType` by id, then **reflectively instantiates** `recordType.getRecordClass()` via its no-arg constructor (falling back to `UnknownRecord` on any failure) and calls `parseData`. Consequences:
- **To add support for a new record type:** create a `class XyzRecord extends AbstractRecord` in the records package with a public no-arg constructor and `parseData` implementation, then change that type's entry in the `RecordType` enum from `UnknownRecord.class` to `XyzRecord.class`. The enum is the wiring; no other registration exists.
- Types listed in `RecordType` with `UnknownRecord.class` are "known id, not parsed" — they decode to `UnknownRecord` with the correct type name. Type ids absent from the enum decode to `UnknownRecord` with a numeric `TYPE_n` name.

## Conventions & gotchas

- `Buffer.readByte()` uses a bare `assert` for bounds — assertions are off by default at runtime, so malformed/short responses can read past intended bounds rather than failing loudly.
- Several constructors swallow socket setup exceptions with `printStackTrace()` (e.g. `DnsConnectionTcp`/`Udp` no-arg constructors), leaving a half-initialized object; `send`/`receive` then guard with `IllegalStateException`.
- `DnsTest` (a `main` in the `dns` package) and the `overview.html` snippet are runnable usage examples, not part of the public API.
- Tests are JUnit 4 (`org.junit.*`), named `*UnitTest`, and live under `src/test/java` mirroring the package layout.
