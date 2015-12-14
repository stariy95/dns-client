package com.kendamasoft.dns;

class DnsTest {

    static public void main(String... args) {
        DnsTest test = new DnsTest();
        test.requestDns("google.com");
    }

    public DnsTest() {
    }

    public void requestDns(String domainName) {
        DnsProtocol.Message request = new MessageBuilder()
                .setName(domainName)
                .setType(DnsProtocol.RecordType.ANY)
                .build();
        DnsProtocol.Message response = null;
        try {
            response = new ConnectionUdp().doRequest(request);
            if (response.header.hasFlag(DnsProtocol.Header.FLAG_TRUNCATION)) {
                response = new ConnectionTcp().doRequest(request);
            }
        } catch (Exception ex) {
            //@todo Log.w("DNS_TEST", ex);
            return;
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
}
