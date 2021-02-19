# DNS Client

[![Verify build](https://github.com/stariy95/dns-client/actions/workflows/build.yml/badge.svg)](https://github.com/stariy95/dns-client/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.kendamasoft/dns-client.svg?colorB=brightgreen)](https://search.maven.org/artifact/com.kendamasoft/dns-client)

Compact DNS client library intended primary for network utilities and testing applications.
It is fully compatible with <b>Android 2.3</b> and newer and with standalone <b>JRE 7</b> and newer.

## Release Notes

### 1.1.0

- Initial support for DNS-over-HTTPS protocol
- Support additional records types

### 1.0.0

- Initial release

## Installation:

### Gradle
```
dependencies {
    compile 'com.kendamasoft:dns-client:1.1.0'
}
```

### Maven
```
<dependency>
   <groupId>com.kendamasoft</groupId>
   <artifactId>dns-client</artifactId>
   <version>1.1.0</version>
</dependency>
```

## Usage:

```java
import java.io.IOException;
import com.kendamasoft.dns.protocol.*;

public class DnsTest {

    static public void main(String... args) throws IOException {
        Message request = new MessageBuilder()
                .setName("example.com")
                .setType(RecordType.ANY)
                .build();

        Message response = new DnsConnectionAuto().doRequest(request);
        for (ResourceRecord record : response.getAllRecords()) {
            System.out.println(record);
        }
    }
}
```
