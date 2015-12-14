# DNS Client

DNS Client is a small DNS client library intended primary for network utilities and testing applications.
It is fully compatible with <b>Android 2.3</b> and newer and with standalone <b>JRE 6</b> and newer.

## Installation:

### Gradle

Add as a dependency to your ```build.gradle```:
```
repositories {
    maven {
        url  "http://dl.bintray.com/stariy95/maven"
    }
}

...

dependencies {
    compile(group: 'com.kendamasoft', name: 'dns-client', version: '0.9.3', ext: 'jar')
}
```

## Usage:
```java
    ...

    void lookupDns() throws IOException {

        DnsProtocol.Message request = new MessageBuilder()
                .setName("example.com")
                .setType(DnsProtocol.RecordType.ANY)
                .build();

        DnsProtocol.Message response = new DnsConnectionUdp().doRequest(request);
        if (response.header.hasFlag(DnsProtocol.Header.FLAG_TRUNCATION)) {
            response = new DnsConnectionTcp().doRequest(request);
        }

        if(response.getHeader().getAnswerResourceRecordsCount() > 0) {
            System.out.println("ANSWER:");
            for(DnsProtocol.ResourceRecord record : response.getAnswerRecordList()) {
                System.out.println(record);
            }
        }

        if(response.getHeader().getAuthorityResourceRecordsCount() > 0) {
            System.out.println("AUTHORITY:");
            for(DnsProtocol.ResourceRecord record : response.getAuthorityRecordList()) {
                System.out.println(record);
            }
        }

        if(response.getHeader().getAdditionalResourceRecordsCount() > 0) {
            System.out.println("ADDITIONAL:");
            for(DnsProtocol.ResourceRecord record : response.getAdditionalRecordList()) {
                System.out.println(record);
            }
        }
    }

    ...
```