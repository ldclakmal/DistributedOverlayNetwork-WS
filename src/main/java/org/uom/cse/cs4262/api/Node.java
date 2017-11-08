package org.uom.cse.cs4262.api;

import org.uom.cse.cs4262.api.message.request.SearchRequest;
import org.uom.cse.cs4262.api.message.response.SearchResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Chamin Wickramarathna
 * @date 22/10/2017
 * @since 1.0
 */
public class Node {

    private Credential credential;
    private List<String> fileList;
    private List<Credential> routingTable;
    private List<StatRecord> statTable;
    private Credential bootstrap;
    private int receivedQueryCount;
    private int forwardedQueryCount;
    private int answeredQueryCount;
    private int searchedQueryCount;
    private HashMap<String, ArrayList<String>> displayTable;
    private List<QueryRecord> queryTable;

    private int receivedRequestCount;
    private int forwardedRequestCount;
    private int answeredResponseCount;
    private int searchedRequestCount;
    private float requestSuccessRatio;
    private float avgLatency;
    private float avgHopCount;


    public Node(Credential credential, List<String> fileList, List<Credential> routingTable, List<StatRecord> statTable, Credential bootstrap, int receivedQueryCount, int forwardedQueryCount, int answeredQueryCount, int searchedQueryCount, HashMap<String,ArrayList<String>> displayTable, List<QueryRecord> queryTable) {
        this.credential = credential;
        this.fileList = fileList;
        this.routingTable = routingTable;
        this.statTable = statTable;
        this.bootstrap = bootstrap;
        this.receivedQueryCount = receivedQueryCount;
        this.forwardedQueryCount = forwardedQueryCount;
        this.answeredQueryCount = answeredQueryCount;
        this.searchedQueryCount = searchedQueryCount;
        this.displayTable = displayTable;
        this.queryTable = queryTable;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    public List<Credential> getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(List<Credential> routingTable) {
        this.routingTable = routingTable;
    }

    public List<StatRecord> getStatTable() {
        return statTable;
    }

    public void setStatTable(List<StatRecord> statTable) {
        this.statTable = statTable;
    }

    public Credential getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(Credential bootstrap) {
        this.bootstrap = bootstrap;
    }

    public int getReceivedQueryCount() {
        return receivedQueryCount;
    }

    public void setReceivedQueryCount(int receivedQueryCount) {
        this.receivedQueryCount = receivedQueryCount;
    }

    public int getForwardedQueryCount() {
        return forwardedQueryCount;
    }

    public void setForwardedQueryCount(int forwardedQueryCount) {
        this.forwardedQueryCount = forwardedQueryCount;
    }

    public int getAnsweredQueryCount() {
        return answeredQueryCount;
    }

    public void setAnsweredQueryCount(int answeredQueryCount) {
        this.answeredQueryCount = answeredQueryCount;
    }

    public int getSearchedQueryCount() {
        return searchedQueryCount;
    }

    public void setSearchedQueryCount(int searchedQueryCount) {
        this.searchedQueryCount = searchedQueryCount;
    }

    public HashMap<String, ArrayList<String>> getDisplayTable() {
        return displayTable;
    }

    public void setDisplayTable(HashMap<String, ArrayList<String>> displayTable) {
        this.displayTable = displayTable;
    }

    public List<QueryRecord> getQueryTable() {
        return queryTable;
    }

    public void setQueryTable(List<QueryRecord> queryTable) {
        this.queryTable = queryTable;
    }

    public int getReceivedRequestCount() {
        return receivedRequestCount;
    }

    public void setReceivedRequestCount(int receivedRequestCount) {
        this.receivedRequestCount = receivedRequestCount;
    }

    public int getForwardedRequestCount() {
        return forwardedRequestCount;
    }

    public void setForwardedRequestCount(int forwardedRequestCount) {
        this.forwardedRequestCount = forwardedRequestCount;
    }

    public int getAnsweredResponseCount() {
        return answeredResponseCount;
    }

    public void setAnsweredResponseCount(int answeredResponseCount) {
        this.answeredResponseCount = answeredResponseCount;
    }

    public int getSearchedRequestCount() {
        return searchedRequestCount;
    }

    public void setSearchedRequestCount(int searchedRequestCount) {
        this.searchedRequestCount = searchedRequestCount;
    }

    public float getRequestSuccessRatio() {
        return requestSuccessRatio;
    }

    public void setRequestSuccessRatio(float requestSuccessRatio) {
        this.requestSuccessRatio = requestSuccessRatio;
    }

    public float getAvgLatency() {
        return avgLatency;
    }

    public void setAvgLatency(float avgLatency) {
        this.avgLatency = avgLatency;
    }

    public float getAvgHopCount() {
        return avgHopCount;
    }

    public void setAvgHopCount(float avgHopCount) {
        this.avgHopCount = avgHopCount;
    }

    public float getAverageLatency(){
        long totalDifference = 0;
        for(int i=0; i<this.statTable.size();i++){
            StatRecord statRecord = this.statTable.get(i);
            totalDifference += getDateDiff(statRecord.getTriggeredTime(),statRecord.getDeliveryTime());
        }
        avgLatency = totalDifference/searchedQueryCount;
        return avgLatency;
    }

    public long getDateDiff(Date date1, Date date2) {
        //time difference will return in seconds
        TimeUnit timeUnit = TimeUnit.SECONDS;
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
