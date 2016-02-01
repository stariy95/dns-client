package com.kendamasoft.dns.protocol;

/**
 * DNS resource question query. <br>
 * Not directly usable.
 */
final class QuestionEntry {
    static final short QUESTION_CLASS_IN = 0x0001;

    /**
     * Resource name (typically domain name we want to query)
     */
    String name;

    /**
     * Record type we want to know
     * @see RecordType
     */
    short type;

    /**
     * @see QuestionEntry#QUESTION_CLASS_IN
     */
    short questionClass;
}
