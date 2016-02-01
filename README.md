# DNS Client

Compact DNS client library intended primary for network utilities and testing applications.
It is fully compatible with <b>Android 2.3</b> and newer and with standalone <b>JRE 6</b> and newer.

## Installation:

### Gradle
```
dependencies {
    compile 'com.kendamasoft:dns-client:1.0.0'
}
```

### Maven
```
<dependency>
   <groupId>com.kendamasoft</groupId>
   <artifactId>dns-client</artifactId>
   <version>1.0.0</version>
</dependency>
```

## Usage:
```java
    ...

    Message request = new MessageBuilder()
            .setName("example.com")
            .setType(DnsProtocol.RecordType.ANY)
            .build();

    Message response = new DnsConnectionAuto().doRequest(request);
    for(DnsProtocol.ResourceRecord record : response.getAllRecords()) {
        System.out.println(record);
    }
    ...
```