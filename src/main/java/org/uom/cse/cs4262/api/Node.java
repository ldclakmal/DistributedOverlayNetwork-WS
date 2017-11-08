package org.uom.cse.cs4262.api;

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
    private HashMap<String, ArrayList<String>> displayTable;
    private List<QueryRecord> queryTable;

    public Node(Credential credential, List<String> fileList, List<Credential> routingTable, List<StatRecord> statTable, Credential bootstrap, int receivedQueryCount, int forwardedQueryCount, int answeredQueryCount, int searchedQueryCount, HashMap<String, ArrayList<String>> displayTable, List<QueryRecord> queryTable) {
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
}
