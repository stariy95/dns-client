# DNS Client

DNS Client is a small DNS client library intended primary for network utilities and testing applications.
It is fully compatible with Android 2.3</b> and newer and with standalone <b>JRE 6</b> and newer.

## Installation:

### Gradle

Add as a dependency to your ```build.gradle```:
```
dependencies {
    compile 'com.kendamasoft.dns-client:0.9.7'
}
```

### Maven

TBD

## Usage:
```java
    ...

    void lookupDns() throws Exception {

        DnsProtocol.Message request = new MessageBuilder()
                .setName("example.com")
                .setType(DnsProtocol.RecordType.ANY)
                .build();

        DnsProtocol.Message response = new ConnectionUdp().doRequest(request);
        if (response.header.hasFlag(DnsProtocol.Header.FLAG_TRUNCATION)) {
            response = new ConnectionTcp().doRequest(request);
        }

        if(response.header.answerResourceRecordCount > 0) {
            System.out.println("ANSWER:");
            for(DnsProtocol.ResourceRecord record : response.answerRecordList) {
                System.out.println(record); // @todo replace
            }
        }

        if(response.header.authorityResourceRecordsCount > 0) {
            System.out.println("AUTHORITY:");
            for(DnsProtocol.ResourceRecord record : response.authorityRecordList) {
                System.out.println(record); // @todo replace
            }
        }

        if(response.header.additionalResourceRecordsCount > 0) {
            System.out.println("ADDITIONAL:");
            for(DnsProtocol.ResourceRecord record : response.additionalRecordList) {
                System.out.println(record); // @todo replace
            }
        }
    }

    ...
```