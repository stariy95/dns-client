# DNS Client

[![Verify build](https://github.com/stariy95/dns-client/actions/workflows/build.yml/badge.svg)](https://github.com/stariy95/dns-client/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.kendamasoft/dns-client.svg?colorB=brightgreen)](https://search.maven.org/artifact/com.kendamasoft/dns-client)

Compact DNS client library intended primarily for network utilities and testing applications.
It is fully compatible with **Android 5.0** (API 21) and newer and with standalone **JRE 8** and newer.

## Features

- **Transports:** UDP (`DnsConnectionUdp`), TCP (`DnsConnectionTcp`), DNS-over-HTTPS (`DnsConnectionDoh`), and automatic UDP&rarr;TCP fallback on truncation (`DnsConnectionAuto`).
- **Supported record types:** `A`, `AAAA`, `NS`, `CNAME`, `SOA`, `PTR`, `MX`, `TXT`, `HINFO`, `SRV`, `NAPTR`, `KX`, `DNAME`, `URI`, `CAA`, `SPF`, `EUI48`, `EUI64`, plus DNSSEC and service-binding types `DS`, `DNSKEY`, `RRSIG`, `NSEC`, `NSEC3`, `CDS`, `CDNSKEY`, `SSHFP`, `TLSA`, `SMIMEA`, `SVCB`, `HTTPS`. Other known types decode to a generic `UnknownRecord`.
- **Zero runtime dependencies.**

## Release Notes

### 1.2.0

- Full support for additional record types: `HTTPS`, `SVCB`, `RRSIG`, `NSEC`, `NSEC3`
- Expanded DNSSEC and service-binding record parsing

### 1.1.0

- Initial support for DNS-over-HTTPS protocol
- Support additional records types

### 1.0.0

- Initial release

## Installation

### Gradle
```
dependencies {
    implementation 'com.kendamasoft:dns-client:1.2.0'
}
```

### Maven
```
<dependency>
   <groupId>com.kendamasoft</groupId>
   <artifactId>dns-client</artifactId>
   <version>1.2.0</version>
</dependency>
```

## Usage

```java
import java.io.IOException;
import com.kendamasoft.dns.*;
import com.kendamasoft.dns.protocol.*;

public class DnsTest {

    static public void main(String... args) throws IOException {
        Message request = new MessageBuilder()
                .setName("example.com")
                .setType(RecordType.A)
                .build();

        Message response = new DnsConnectionAuto().doRequest(request);
        for (ResourceRecord record : response.getAllRecords()) {
            System.out.println(record);
        }
    }
}
```

`DnsConnectionAuto` sends over UDP and transparently retries over TCP when the response is
truncated. Any of the following connections is a drop-in replacement for `new DnsConnectionAuto()`
above if you want to pick a transport or resolver explicitly:

```java
new DnsConnectionUdp();                                  // UDP, default resolver (Google 8.8.8.8)
new DnsConnectionUdp(InetAddress.getByName("1.1.1.1"));  // UDP against a specific resolver
new DnsConnectionTcp();                                  // TCP, default resolver
new DnsConnectionDoh("https://1.1.1.1/dns-query");       // DNS-over-HTTPS (endpoint must be https)
```
