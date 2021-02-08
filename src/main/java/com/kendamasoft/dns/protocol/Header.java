package com.kendamasoft.dns.protocol;

/**
 * DNS Message Header<br>
 * <code>
 * 2 octet transaction Id<br>
 * 2 octet flags<br>
 * 2 octet question resource count<br>
 * 2 octet answer resource count<br>
 * 2 octet authority resource count<br>
 * 2 octet additional resource count<br>
 * </code>
 * @see Header#flags
 */
public final class Header {

    /**
     * 0 - request, 1 - response
     */
    static public final int FLAG_MESSAGE_TYPE           = (1 << 15);
    static public final int FLAG_AUTHORITATIVE_RESPONSE = (1 << 10);
    /**
     * 1 - data truncation, should switch connection from UDP to TCP
     */
    static public final int FLAG_TRUNCATION             = (1 << 9);
    static public final int FLAG_RECURSION_DESIRED      = (1 << 8);
    static public final int FLAG_RECURSION_AVAILABLE    = (1 << 7);

    static public final int ERROR_FORMAT          = 0x1;
    static public final int ERROR_SERVER_FAILURE  = 0x2;
    static public final int ERROR_NAME            = 0x3;
    static public final int ERROR_NOT_IMPLEMENTED = 0x4;
    static public final int ERROR_REFUSED         = 0x5;

    short transactionId;

    /**
     * Flags bit layout (from most significant bit to less)<br>
     * <code>
     * first octet
     * [0]   - req [=0] / res [=1]
     * [1,4] - op code ([=0] for query)
     * [5]   - authoritative response [=1]
     * [6]   - truncation (if [=1] - exceeded limit in 512b)
     * [7]   - recursion desired [=1]
     * second octet
     * [0]   - recursion available
     * [1-3] - reserved
     * [4-7] - return code ([=0] success, [=0x3] error)
     * </code>
     */
    short flags;

    short questionResourceRecordCount;

    short answerResourceRecordsCount;

    short authorityResourceRecordsCount;

    short additionalResourceRecordsCount;

    public short getAnswerResourceRecordsCount() {
        return answerResourceRecordsCount;
    }

    public short getAuthorityResourceRecordsCount() {
        return authorityResourceRecordsCount;
    }

    public short getAdditionalResourceRecordsCount() {
        return additionalResourceRecordsCount;
    }

    /**
     * @see Header#FLAG_MESSAGE_TYPE
     * @see Header#FLAG_AUTHORITATIVE_RESPONSE
     * @see Header#FLAG_TRUNCATION
     * @see Header#FLAG_RECURSION_DESIRED
     * @see Header#FLAG_RECURSION_AVAILABLE
     * @param flag to check
     * @return is flag set
     */
    public boolean hasFlag(int flag) {
        return ((flags & 0xffff) & flag) > 0;
    }

    /**
     * @return operation code (zero for query)
     */
    public int opCode() {
        // 0b0111_0000_0000_0000;
        return flags & 0x7000;
    }

    /**
     * @return return code (zero - success, 0x3 - error)
     */
    public int returnCode() {
        // 0b0000_0111
        return flags & 0xf;
    }
}
