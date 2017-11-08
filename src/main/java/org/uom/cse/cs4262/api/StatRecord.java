package org.uom.cse.cs4262.api;

import java.util.Date;
import java.util.List;

/**
 * @author Chamin Wickramarathna
 * @date 22/10/2017
 * @since 1.0
 */
public class StatRecord {
    private String searchQuery;
    private Date triggeredTime;
    private Date deliveryTime;
    private int hopsRequired;
    private Credential servedNode;
    private List<String> fileList;

    public StatRecord(String searchQuery, Date triggeredTime, Date deliveryTime, int hopsRequired, Credential servedNode, List<String> fileList) {
        this.searchQuery = searchQuery;
        this.triggeredTime = triggeredTime;
        this.deliveryTime = deliveryTime;
        this.hopsRequired = hopsRequired;
        this.servedNode = servedNode;
        this.fileList = fileList;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
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

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getHopsRequired() {
        return hopsRequired;
    }

    public void setHopsRequired(int hopsRequired) {
        this.hopsRequired = hopsRequired;
    }

    public Credential getServedNode() {
        return servedNode;
    }

    public void setServedNode(Credential servedNode) {
        this.servedNode = servedNode;
    }
}
