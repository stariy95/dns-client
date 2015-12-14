package com.kendamasoft.dns;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builder class for DNS {@link com.kendamasoft.dns.DnsProtocol.Message message}<br>
 * Usage:<br>
 * <pre><code>
 *     ...
 *     DnsProtocol.Message message = new MessageBuilder()
 *              .setName("example.com")
 *              .setType(DnsProtocol.RecordType.A)
 *              .build();
 *     ...
 * </code></pre>
 */
public class MessageBuilder {

    static private AtomicInteger id = new AtomicInteger();

    private String name;

    private DnsProtocol.RecordType type = DnsProtocol.RecordType.A;

    public MessageBuilder() {
    }

    /**
     * @param name of domain to lookup
     * @return builder
     */
    public MessageBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param type of resource records to lookup
     * @return builder
     */
    public MessageBuilder setType(DnsProtocol.RecordType type) {
        this.type = type;
        return this;
    }

    private DnsProtocol.Header  buildHeader() {
        DnsProtocol.Header header = new DnsProtocol.Header();
        header.transactionId = (short)id.incrementAndGet();
        header.flags = DnsProtocol.Header.FLAG_RECURSION_DESIRED;
        header.questionResourceRecordCount = 1;
        return header;
    }

    private DnsProtocol.QuestionEntry buildEntry() {
        DnsProtocol.QuestionEntry questionEntry = new DnsProtocol.QuestionEntry();
        questionEntry.name = name;
        questionEntry.type = type.getId();
        questionEntry.questionClass = DnsProtocol.QUESTION_CLASS_IN;
        return questionEntry;
    }

    /**
     * @return build message ready to send
     */
    public DnsProtocol.Message build() {
        DnsProtocol.Message message = new DnsProtocol.Message();
        message.header = buildHeader();
        message.questionEntry = buildEntry();
        return message;
    }
}
