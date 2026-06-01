package com.kendamasoft.dns;

import com.kendamasoft.dns.protocol.Message;
import com.kendamasoft.dns.protocol.MessageBuilder;
import com.kendamasoft.dns.protocol.RecordType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Opt-in live-network tests that query real public resolvers. Tagged {@code "integration"}
 * and excluded from the default build (and CI); run them with:
 * <pre>{@code ./gradlew test -Pintegration}</pre>
 * Assertions are intentionally loose (the lookup completes and returns at least one record),
 * since live responses vary by network and resolver.
 */
@Tag("integration")
public class LiveResolverIntegrationTest {

    private static Message exampleQuery() {
        return new MessageBuilder().setName("example.com").setType(RecordType.A).build();
    }

    @Test
    public void udpResolvesExampleCom() throws Exception {
        Message response = new DnsConnectionUdp().doRequest(exampleQuery());
        assertFalse(response.getAllRecords().isEmpty());
    }

    @Test
    public void tcpResolvesExampleCom() throws Exception {
        Message response = new DnsConnectionTcp().doRequest(exampleQuery());
        assertFalse(response.getAllRecords().isEmpty());
    }

    @Test
    public void dohResolvesExampleCom() throws Exception {
        Message response = new DnsConnectionDoh("https://1.1.1.1/dns-query").doRequest(exampleQuery());
        assertFalse(response.getAllRecords().isEmpty());
    }

    @Test
    public void autoResolvesExampleCom() throws Exception {
        Message response = new DnsConnectionAuto().doRequest(exampleQuery());
        assertFalse(response.getAllRecords().isEmpty());
    }
}
