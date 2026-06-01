package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;
import com.kendamasoft.dns.protocol.RecordType;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Internal RDATA parsing/formatting helpers shared by the record content classes.
 * <p>
 * Hex, Base64 and Base32hex are hand-rolled on purpose: {@code java.util.Base64} is a Java&nbsp;8
 * runtime API that is only available on Android API&nbsp;26+, below this library's API&nbsp;21 floor.
 * <b><i>Only for internal use.</i></b>
 *
 * @since 1.2.0
 */
final class RecordData {

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private static final char[] BASE64 =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    private static final char[] BASE32HEX = "0123456789ABCDEFGHIJKLMNOPQRSTUV".toCharArray();

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

    /**
     * RFC 4648 base32hex, uppercase and unpadded - the presentation form of the NSEC3 next hashed
     * owner name.
     */
    static String toBase32Hex(byte[] data) {
        StringBuilder sb = new StringBuilder((data.length * 8 + 4) / 5);
        int buffer = 0;
        int bits = 0;
        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xff);
            bits += 8;
            while (bits >= 5) {
                bits -= 5;
                sb.append(BASE32HEX[(buffer >> bits) & 0x1f]);
            }
            buffer &= (1 << bits) - 1;
        }
        if (bits > 0) {
            sb.append(BASE32HEX[(buffer << (5 - bits)) & 0x1f]);
        }
        return sb.toString();
    }

    /**
     * Read the trailing RDATA bytes that follow the already-consumed (possibly variable-length) fields.
     *
     * @param startPosition the {@link Buffer#position()} captured at the start of {@code parseData}
     */
    static byte[] restOfRecord(Buffer buffer, int dataLength, int startPosition) {
        return remaining(buffer, dataLength - (buffer.position() - startPosition));
    }

    /**
     * Resource Record type mnemonic for a numeric id, or {@code "TYPE_n"} when unknown - matching
     * {@link UnknownRecord}'s naming.
     */
    static String typeName(int id) {
        RecordType type = RecordType.getById((short) id);
        return type != null ? type.name() : "TYPE_" + id;
    }

    /**
     * Decode an NSEC/NSEC3 type bit map (RFC 4034 4.1.2) into ascending Resource Record type ids.
     */
    static List<Integer> bitmapTypes(byte[] map) {
        List<Integer> types = new ArrayList<>();
        int i = 0;
        while (i + 2 <= map.length) {
            int window = map[i++] & 0xff;
            int length = map[i++] & 0xff;
            for (int octet = 0; octet < length && i < map.length; octet++, i++) {
                int bits = map[i] & 0xff;
                for (int bit = 0; bit < 8; bit++) {
                    if ((bits & (0x80 >> bit)) != 0) {
                        types.add(window * 256 + octet * 8 + bit);
                    }
                }
            }
        }
        return types;
    }

    /**
     * Format an RRSIG inception/expiration time (u32 seconds since the Unix epoch) as the
     * {@code yyyyMMddHHmmss} UTC string used by {@code dig}.
     */
    static String sigTime(long epochSeconds) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date(epochSeconds * 1000L));
    }

    /**
     * Dotted-decimal IPv4 from 4 bytes.
     */
    static String ipv4(byte[] address) {
        StringBuilder sb = new StringBuilder(15);
        for (int i = 0; i < address.length; i++) {
            if (i > 0) {
                sb.append('.');
            }
            sb.append(address[i] & 0xff);
        }
        return sb.toString();
    }

    /**
     * JDK-formatted IPv6 from 16 bytes (empty string if the length is invalid).
     */
    static String ipv6(byte[] address) {
        try {
            return InetAddress.getByAddress(address).getHostAddress();
        } catch (Exception ex) {
            return "";
        }
    }
}
