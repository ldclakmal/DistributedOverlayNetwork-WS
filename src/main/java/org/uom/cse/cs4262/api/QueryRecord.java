package org.uom.cse.cs4262.api;

import java.util.Date;

/**
 * @author Sachithra Dangalla
 * @date 11/8/2017
 * @since 1.0
 */
public class QueryRecord {
    private int sequenceNo;
    private String searchQuery;
    private Date triggeredTime;

    public QueryRecord(int sequenceNo, String searchQuery, Date triggeredTime) {
        this.sequenceNo = sequenceNo;
        this.searchQuery = searchQuery;
        this.triggeredTime = triggeredTime;
    }

    public QueryRecord(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Date getTriggeredTime() {
        return triggeredTime;
    }

    public void setTriggeredTime(Date triggeredTime) {
        this.triggeredTime = triggeredTime;
    }
}
