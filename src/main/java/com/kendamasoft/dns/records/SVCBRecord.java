package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service Binding record (RFC 9460)
 * @since 1.2.0
 */
public class SVCBRecord extends AbstractRecord {

    /**
     * A single SvcParam: its numeric key plus the raw (still-encoded) value bytes.
     */
    public static class SvcParam {
        final int key;
        final byte[] value;

        SvcParam(int key, byte[] value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public byte[] getValue() {
            return value;
        }
    }

    int svcPriority;

    String targetName;

    final List<SvcParam> params = new ArrayList<>();

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        int start = buffer.position();
        svcPriority = buffer.readShort() & 0xFFFF;
        targetName  = buffer.readString();
        while (buffer.position() - start < dataLength) {
            int key = buffer.readShort() & 0xFFFF;
            int len = buffer.readShort() & 0xFFFF;
            params.add(new SvcParam(key, RecordData.remaining(buffer, len)));
        }
    }

    public int getSvcPriority() {
        return svcPriority;
    }

    public String getTargetName() {
        return targetName;
    }

    public List<SvcParam> getParams() {
        return Collections.unmodifiableList(params);
    }

    /**
     * @return record type name used in {@link #toString()}; overridden by {@link HTTPSRecord}.
     */
    protected String recordName() {
        return "SVCB";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(recordName()).append(' ')
                .append(svcPriority).append(' ').append(targetName);
        for (SvcParam param : params) {
            sb.append(' ').append(renderParam(param));
        }
        return sb.toString();
    }

    private static String renderParam(SvcParam param) {
        byte[] v = param.value;
        switch (param.key) {
            case 0:  return "mandatory=" + keyList(v);
            case 1:  return "alpn=\"" + alpnList(v) + "\"";
            case 2:  return "no-default-alpn";
            case 3:  return "port=" + (((v[0] & 0xff) << 8) | (v[1] & 0xff));
            case 4:  return "ipv4hint=" + ipList(v, 4);
            case 5:  return "ech=\"" + RecordData.toBase64(v) + "\"";
            case 6:  return "ipv6hint=" + ipList(v, 16);
            default: return svcKeyName(param.key) + "=" + RecordData.toHex(v);
        }
    }

    private static String svcKeyName(int key) {
        switch (key) {
            case 0:  return "mandatory";
            case 1:  return "alpn";
            case 2:  return "no-default-alpn";
            case 3:  return "port";
            case 4:  return "ipv4hint";
            case 5:  return "ech";
            case 6:  return "ipv6hint";
            default: return "key" + key;
        }
    }

    /** A list of u16 SvcParamKeys (the value format of the {@code mandatory} key). */
    private static String keyList(byte[] v) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 2 <= v.length; i += 2) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(svcKeyName(((v[i] & 0xff) << 8) | (v[i + 1] & 0xff)));
        }
        return sb.toString();
    }

    /** A sequence of length-prefixed character-strings (the value format of the {@code alpn} key). */
    private static String alpnList(byte[] v) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        boolean first = true;
        while (i < v.length) {
            int len = v[i++] & 0xff;
            if (!first) {
                sb.append(',');
            }
            first = false;
            for (int j = 0; j < len && i < v.length; j++, i++) {
                sb.append((char)(v[i] & 0xff));
            }
        }
        return sb.toString();
    }

    /** A list of fixed-width IP address hints ({@code groupSize} = 4 for IPv4, 16 for IPv6). */
    private static String ipList(byte[] v, int groupSize) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + groupSize <= v.length; i += groupSize) {
            byte[] group = new byte[groupSize];
            System.arraycopy(v, i, group, 0, groupSize);
            if (i > 0) {
                sb.append(',');
            }
            sb.append(groupSize == 4 ? RecordData.ipv4(group) : RecordData.ipv6(group));
        }
        return sb.toString();
    }
}
