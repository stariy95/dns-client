package com.kendamasoft.dns.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builder class for DNS {@link Message message}<br>
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

    private RecordType type = RecordType.A;

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
    public MessageBuilder setType(RecordType type) {
        this.type = type;
        return this;
    }

    private Header buildHeader() {
        Header header = new Header();
        header.transactionId = (short)id.incrementAndGet();
        header.flags = Header.FLAG_RECURSION_DESIRED;
        header.questionResourceRecordCount = 1;
        return header;
    }

    private QuestionEntry buildEntry() {
        QuestionEntry questionEntry = new QuestionEntry();
        questionEntry.name = name;
        questionEntry.type = type.getId();
        questionEntry.questionClass = QuestionEntry.QUESTION_CLASS_IN;
        return questionEntry;
    }

    /**
     * @return build message ready to send
     */
    public Message build() {
        Message message = new Message();
        message.header = buildHeader();
        message.questionEntry = buildEntry();
        return message;
    }
}
