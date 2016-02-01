package com.kendamasoft.dns;

import com.kendamasoft.dns.protocol.*;

final class DnsTest {

    static public void main(String... args) {
        DnsTest test = new DnsTest();
        test.requestDns("google.com");
    }

    private DnsTest() {
    }

    private void requestDns(String domainName) {
        Message request = new MessageBuilder()
                .setName(domainName)
                .setType(RecordType.ANY)
                .build();

        Message response;
        try {
            response = new DnsConnectionAuto().doRequest(request);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        for(ResourceRecord record : response.getAllRecords()) {
            System.out.println(record);
        }
    }
}
