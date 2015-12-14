package com.kendamasoft.dns;

class DnsTest {

    static public void main(String... args) {
        DnsTest test = new DnsTest();
        test.requestDns("example.com");
    }

    public DnsTest() {
    }

    public void requestDns(String domainName) {
        DnsProtocol.Message request = new MessageBuilder()
                .setName(domainName)
                .setType(DnsProtocol.RecordType.ANY)
                .build();

        DnsProtocol.Message response;
        try {
            response = new DnsConnectionUdp().doRequest(request);
            if (response.header.hasFlag(DnsProtocol.Header.FLAG_TRUNCATION)) {
                response = new DnsConnectionTcp().doRequest(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
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
}
