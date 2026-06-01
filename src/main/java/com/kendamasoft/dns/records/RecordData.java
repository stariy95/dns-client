package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * Internal RDATA parsing/formatting helpers shared by the record content classes.
 * <p>
 * Hex and Base64 are hand-rolled on purpose: {@code java.util.Base64} is a Java&nbsp;8 runtime API that
 * is only available on Android API&nbsp;26+, below this library's API&nbsp;21 floor.
 * <b><i>Only for internal use.</i></b>
 *
 * @since 1.2.0
 */
final class RecordData {

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private static final char[] BASE64 =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    private RecordData() {
    }

    /**
     * Read a {@code <character-string>} (RFC 1035 3.3): a single length octet followed by that many bytes.
     */
    static String characterString(Buffer buffer) {
        int length = buffer.readByte() & 0xff;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char)(buffer.readByte() & 0xff));
        }
        return sb.toString();
    }

    /**
     * Read {@code length} trailing RDATA bytes (typically {@code dataLength} minus the fixed-size
     * fields already consumed). A negative length is treated as zero.
     */
    static byte[] remaining(Buffer buffer, int length) {
        byte[] out = new byte[Math.max(0, length)];
        buffer.fill(out, out.length);
        return out;
    }

    /**
     * Lowercase hex with no separator, e.g. {@code "0a1bff"}.
     */
    static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(HEX[(b >> 4) & 0xf]);
            sb.append(HEX[b & 0xf]);
        }
        return sb.toString();
    }

    /**
     * Lowercase hex with {@code separator} between octets, e.g. {@code "0a-1b-ff"}.
     */
    static String toHex(byte[] data, char separator) {
        StringBuilder sb = new StringBuilder(Math.max(0, data.length * 3 - 1));
        for (int i = 0; i < data.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(HEX[(data[i] >> 4) & 0xf]);
            sb.append(HEX[data[i] & 0xf]);
        }
        return sb.toString();
    }

    /**
     * Standard Base64 (RFC 4648) with {@code '='} padding.
     */
    static String toBase64(byte[] data) {
        StringBuilder sb = new StringBuilder(((data.length + 2) / 3) * 4);
        int i = 0;
        while (i + 3 <= data.length) {
            int n = ((data[i] & 0xff) << 16) | ((data[i + 1] & 0xff) << 8) | (data[i + 2] & 0xff);
            sb.append(BASE64[(n >> 18) & 0x3f]);
            sb.append(BASE64[(n >> 12) & 0x3f]);
            sb.append(BASE64[(n >> 6) & 0x3f]);
            sb.append(BASE64[n & 0x3f]);
            i += 3;
        }
        int remaining = data.length - i;
        if (remaining == 1) {
            int n = (data[i] & 0xff) << 16;
            sb.append(BASE64[(n >> 18) & 0x3f]);
            sb.append(BASE64[(n >> 12) & 0x3f]);
            sb.append("==");
        } else if (remaining == 2) {
            int n = ((data[i] & 0xff) << 16) | ((data[i + 1] & 0xff) << 8);
            sb.append(BASE64[(n >> 18) & 0x3f]);
            sb.append(BASE64[(n >> 12) & 0x3f]);
            sb.append(BASE64[(n >> 6) & 0x3f]);
            sb.append('=');
        }
        return sb.toString();
    }
}
