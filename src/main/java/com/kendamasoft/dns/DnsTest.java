package com.kendamasoft.dns;

import com.kendamasoft.dns.protocol.Message;
import com.kendamasoft.dns.protocol.MessageBuilder;
import com.kendamasoft.dns.protocol.RecordType;
import com.kendamasoft.dns.protocol.ResourceRecord;

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
                .setType(RecordType.MX)
                .build();

        Message response;
        try {
            response = new DnsConnectionDoh("https://1.1.1.1/dns-query").doRequest(request);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        for(ResourceRecord record : response.getAllRecords()) {
            System.out.println(record);
        }
    }
}
