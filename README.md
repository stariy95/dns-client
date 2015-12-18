# DNS Client

Small DNS client library intended primary for network utilities and testing applications.
It is fully compatible with <b>Android 2.3</b> and newer and with standalone <b>JRE 6</b> and newer.

## Installation:

### Gradle
```
dependencies {
    compile 'com.kendamasoft:dns-client:0.9.5'
}
```

### Maven
```
<dependency>
   <groupId>com.kendamasoft</groupId>
   <artifactId>dns-client</artifactId>
   <version>0.9.5</version>
</dependency>
```

## Usage:
```java
    ...

    DnsProtocol.Message request = new MessageBuilder()
            .setName("example.com")
            .setType(DnsProtocol.RecordType.ANY)
            .build();

    DnsProtocol.Message response = new DnsConnectionUdp().doRequest(request);
    if (response.getHeader().hasFlag(DnsProtocol.Header.FLAG_TRUNCATION)) {
        response = new DnsConnectionTcp().doRequest(request);
    }

    for(DnsProtocol.ResourceRecord record : response.getAllRecords()) {
        System.out.println(record);
    }
    ...
```