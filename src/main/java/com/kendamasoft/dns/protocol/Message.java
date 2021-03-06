package com.kendamasoft.dns.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * DNS protocol message model
 */
public final class Message {

    private static final Comparator<ResourceRecord> COMPARATOR = new ResourceRecordComparator();

    Header header;
    QuestionEntry questionEntry;
    List<ResourceRecord> answerRecordList;
    List<ResourceRecord> authorityRecordList;
    List<ResourceRecord> additionalRecordList;

    public Header getHeader() {
        return header;
    }

    /**
     * @return answer section resource records
     */
    public List<ResourceRecord> getAnswerRecordList() {
        if(answerRecordList == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(answerRecordList);
    }

    /**
     * @return authority section resource records
     */
    public List<ResourceRecord> getAuthorityRecordList() {
        if(authorityRecordList == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(authorityRecordList);
    }

    /**
     * @return additional section resource records
     */
    public List<ResourceRecord> getAdditionalRecordList() {
        if(additionalRecordList == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(additionalRecordList);
    }

    /**
     * Get all returned Resource Records sorted by type.
     * If there are no records will return empty list.
     * @return all resource records
     * @see Message#getAnswerRecordList()
     * @see Message#getAuthorityRecordList()
     * @see Message#getAdditionalRecordList()
     */
    public List<ResourceRecord> getAllRecords() {
        List<ResourceRecord> result = new ArrayList<>();
        if(answerRecordList != null) {
            result.addAll(answerRecordList);
        }
        if(authorityRecordList != null) {
            result.addAll(authorityRecordList);
        }
        if(additionalRecordList != null) {
            result.addAll(additionalRecordList);
        }
        Collections.sort(result, COMPARATOR);
        return result;
    }

    private static final class ResourceRecordComparator implements Comparator<ResourceRecord> {
        @Override
        public int compare(ResourceRecord o1, ResourceRecord o2) {
            return o1.recordTypeId - o2.recordTypeId;
        }
    }
}
