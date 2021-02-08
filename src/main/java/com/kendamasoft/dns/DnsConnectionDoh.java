package com.kendamasoft.dns;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 * DNS-over-HTTPS connection
 * @since 1.1.0
 */
public class DnsConnectionDoh extends DnsConnection {

    private final String baseUrl;

    private HttpsURLConnection urlConnection;

    public DnsConnectionDoh(String url) {
        if(!url.startsWith("https")) {
            throw new IllegalArgumentException("Only HTTPS protocol supported, got " + url);
        }
        this.baseUrl = url;
    }

    @Override
    protected void send(byte[] request) throws IOException {
        URL url = new URL(baseUrl);
        urlConnection = (HttpsURLConnection)url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setRequestProperty ("User-Agent", "com.kendamasoft.dns-client/1.1.0");
        urlConnection.setRequestProperty ("Content-Type", "application/dns-message");
        urlConnection.setRequestMethod("POST");
        OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write(request);
        outputStream.flush();
    }

    @Override
    protected byte[] receive() throws IOException {
        InputStream inputStream = urlConnection.getInputStream();
        int available = inputStream.available();
        byte[] result = new byte[available];
        int read = inputStream.read(result);
        if(read != available) {
            throw new IOException("Unable to read all input data");
        }
        return result;
    }
}
