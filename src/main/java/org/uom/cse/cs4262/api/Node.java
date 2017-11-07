package org.uom.cse.cs4262.api;

import org.uom.cse.cs4262.api.message.request.SearchRequest;
import org.uom.cse.cs4262.api.message.response.SearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private HashMap<SearchRequest, ArrayList<SearchResponse>> searchTable;

    public Node(Credential credential, List<String> fileList, List<Credential> routingTable, List<StatRecord> statTable, Credential bootstrap, int receivedQueryCount, int forwardedQueryCount, int answeredQueryCount, int searchedQueryCount, HashMap<SearchRequest, ArrayList<SearchResponse>> searchTable) {
        this.credential = credential;
        this.fileList = fileList;
        this.routingTable = routingTable;
        this.statTable = statTable;
        this.bootstrap = bootstrap;
        this.receivedQueryCount = receivedQueryCount;
        this.forwardedQueryCount = forwardedQueryCount;
        this.answeredQueryCount = answeredQueryCount;
        this.searchedQueryCount = searchedQueryCount;
        this.searchTable = searchTable;
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

    public HashMap<SearchRequest, ArrayList<SearchResponse>> getSearchTable() {
        return searchTable;
    }

    public void setSearchTable(HashMap<SearchRequest, ArrayList<SearchResponse>> searchTable) {
        this.searchTable = searchTable;
    }
}
