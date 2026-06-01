package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * SSH Public Key Fingerprint record (RFC 4255)
 * @since 1.2.0
 */
public class SSHFPRecord extends AbstractRecord {

    int algorithm;

    int fingerprintType;

    byte[] fingerprint;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        algorithm       = buffer.readByte() & 0xff;
        fingerprintType = buffer.readByte() & 0xff;
        fingerprint     = RecordData.remaining(buffer, dataLength - 2);
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public int getFingerprintType() {
        return fingerprintType;
    }

    public byte[] getFingerprint() {
        return fingerprint;
    }

    @Override
    public String toString() {
        return "SSHFP " + algorithm + " " + fingerprintType + " " + RecordData.toHex(fingerprint);
    }
}
