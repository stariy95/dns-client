/**
 * DNS Protocol data structures <br> <br>
 *
 * DNS Message in both way consists of <b>{@link com.kendamasoft.dns.protocol.Header}</b>, <b>{@link com.kendamasoft.dns.protocol.QuestionEntry}</b> <br>
 * and zero or any number of <b>{@link com.kendamasoft.dns.protocol.ResourceRecord}</b>
 * <br>
 * <br>
 * <a href="https://www.ietf.org/rfc/rfc1035.txt" target="_blank">https://www.ietf.org/rfc/rfc1035.txt</a> <br>
 * <a href="https://technet.microsoft.com/en-us/library/dd197470(v=ws.10).aspx" target="_blank">https://technet.microsoft.com/en-us/library/dd197470(v=ws.10).aspx</a> <br>
 * <a href="https://en.wikipedia.org/wiki/List_of_DNS_record_types" target="_blank">https://en.wikipedia.org/wiki/List_of_DNS_record_types</a> <br>
 */
package com.kendamasoft.dns.protocol;