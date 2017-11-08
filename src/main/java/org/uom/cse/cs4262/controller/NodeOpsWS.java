package org.uom.cse.cs4262.controller;

import com.google.gson.Gson;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.uom.cse.cs4262.api.*;
import org.uom.cse.cs4262.api.message.Message;
import org.uom.cse.cs4262.api.message.request.*;
import org.uom.cse.cs4262.api.message.response.RegisterResponse;
import org.uom.cse.cs4262.api.message.response.SearchResponse;
import org.uom.cse.cs4262.api.message.response.UnregisterResponse;
import org.uom.cse.cs4262.feature.Parser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Chanaka Lakmal
 * @date 22/10/2017
 * @since 1.0
 */

public class NodeOpsWS implements NodeOps, Runnable {

    RestTemplate restTemplate = new RestTemplate();
    private Node node;
    private DatagramSocket socket;
    private boolean regOk = false;
    private int TTL = 5;
    private List<String> displayLog;
    private boolean logFlag;

    public NodeOpsWS(Node node) {
        this.node = node;
        this.displayLog = new ArrayList<>();
    }

    public Node getNode() {
        return node;
    }

    public List<String> getDisplayLog() {
        return displayLog;
    }

    public boolean isLogFlag() {
        return logFlag;
    }

    public void setLogFlag(boolean logFlag) {
        this.logFlag = logFlag;
    }

    @Override
    public void run() {
        logMe("Server " + this.node.getCredential().getUsername() + " created at " + this.node.getCredential().getPort() + ". Waiting for incoming data...\n");
        byte buffer[];
        DatagramPacket datagramPacket;
        while (true) {
            buffer = new byte[65536];
            datagramPacket = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(datagramPacket);
                String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                Message response = Parser.parse(message);
                processResponse(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // done
    @Override
    public void start() {
        try {
            socket = new DatagramSocket(this.node.getCredential().getPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    // done
    @Override
    public void register() {
        RegisterRequest registerRequest = new RegisterRequest(node.getCredential());
        String msg = registerRequest.getMessageAsString(Constant.Command.REG);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(node.getBootstrap().getIp()), node.getBootstrap().getPort()));
            logMe("Sent REGISTER at " + getCurrentTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // done
    @Override
    public void unRegister() {
        UnregisterRequest unregisterRequest = new UnregisterRequest(node.getCredential());
        String msg = unregisterRequest.getMessageAsString(Constant.Command.UNREG);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(node.getBootstrap().getIp()), node.getBootstrap().getPort()));
            logMe("Sent UNREGISTER at " + getCurrentTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param neighbourCredential Called when I'm 'JOIN'-ing
     */
    // done
    @Override
    public void join(Credential neighbourCredential) {
        JoinRequest joinRequest = new JoinRequest(node.getCredential());
        String msg = joinRequest.getMessageAsString(Constant.Command.JOIN);
        String uri = Constant.HTTP + neighbourCredential.getIp() + ":" + neighbourCredential.getPort() + Constant.UrlPattern.JOIN;
        String result = "";
        try {
            result = restTemplate.postForObject(uri, new Gson().toJson(joinRequest), String.class);
            logMe("Sent JOIN to " + neighbourCredential.getIp() + ":" + neighbourCredential.getPort() + " at " + getCurrentTime());
        } catch (ResourceAccessException exception) {
            //connection refused to the api end point
            if (node.getRoutingTable().contains(neighbourCredential)) {
                node.getRoutingTable().remove(neighbourCredential);
                logMe(neighbourCredential.getIp() + "node is not available. Removed it from routing table.");
            }
            //Todo: Remove this neighbour from stat table
        }
        if (result.equals(Constant.Command.JOINOK)) {
            node.incReceivedQueryCount();
            node.getRoutingTable().add(neighbourCredential);
            logMe("Added " + neighbourCredential.getIp() + ":" + neighbourCredential.getPort() + " to Routing Table");
        }
    }

    /**
     * @param joinRequest Called when I'm listening and someone else sends me a 'join'
     */
    @Override
    public void joinMe(JoinRequest joinRequest) {
        logMe("Received JOIN from " + joinRequest.getCredential().getIp() + ":" + joinRequest.getCredential().getPort() + " at " + getCurrentTime());
        //check if already exist
        if (node.getRoutingTable().contains(joinRequest.getCredential())) {
            logMe("But he's already in...");
        } else {
            node.getRoutingTable().add(joinRequest.getCredential());
            logMe("Added " + joinRequest.getCredential().getIp() + ":" + joinRequest.getCredential().getPort() + " to Routing Table");
        }

    }

    /**
     * Called when I'm 'LEAVE'-ing
     */
    @Override
    public void leave() {
        LeaveRequest leaveRequest = new LeaveRequest(node.getCredential());
        String msg = leaveRequest.getMessageAsString(Constant.Command.LEAVE);
        for (Credential neighbourCredential : node.getRoutingTable()) {
            String uri = Constant.HTTP + neighbourCredential.getIp() + ":" + neighbourCredential.getPort() + Constant.UrlPattern.LEAVE;
            try {
                String result = restTemplate.postForObject(uri, new Gson().toJson(leaveRequest), String.class);
                logMe("Sent LEAVE to my neighbors at " + getCurrentTime());
            } catch (ResourceAccessException exception) {
                //connection refused to the api end point
            }
        }
    }

    /**
     * @param leaveRequest Called when I amd listening and someone else sends me a 'leave'
     */
    @Override
    public void removeMe(LeaveRequest leaveRequest) {
        //check my routing table to see if leaveRequest exist
//        for (Credential credential : node.getRoutingTable()) {
        if (node.getRoutingTable().contains(leaveRequest.getCredential())) {
            node.getRoutingTable().remove(leaveRequest.getCredential());
        }
//        }
        removeFromStatTable(leaveRequest.getCredential());
        logMe("Received LEAVE from " + leaveRequest.getCredential().getUsername() + ":" + leaveRequest.getCredential().getPort() + " at " + getCurrentTime());
    }


    /**
     * @param searchRequest
     * @param sendCredentials API Call to send search request to others
     */
    @Override
    public boolean search(SearchRequest searchRequest, Credential sendCredentials) {
        String msg = searchRequest.getMessageAsString(Constant.Command.SEARCH);
        String uri = Constant.HTTP + sendCredentials.getIp() + ":" + sendCredentials.getPort() + Constant.UrlPattern.SEARCH;
        try {
            String result = restTemplate.postForObject(uri, new Gson().toJson(searchRequest), String.class);
            if (result == "202"){
                return true;
            }
        } catch (ResourceAccessException exception) {
            //connection refused to the api end point
            if (node.getRoutingTable().contains(sendCredentials)) {
                node.getRoutingTable().remove(sendCredentials);
                logMe(sendCredentials.getIp() + "node is not available and removed from routing table.");
            }
            //Todo: Remove this neighbour from stat table
        }
        return false;
    }

    /**
     * @param searchResponse API Call to send SEARCHOK to others
     */
    @Override
    public void searchOk(SearchResponse searchResponse) {
        node.incForwardedQueryCount();
        node.incAnsweredQueryCount();
        String msg = searchResponse.getMessageAsString(Constant.Command.SEARCHOK);

        String uri = Constant.HTTP + searchResponse.getCredential().getIp() + ":" + searchResponse.getCredential().getPort() + Constant.UrlPattern.SEARCHOK;
        try {
            searchResponse.setCredential(node.getCredential());
            String result = restTemplate.postForObject(uri, new Gson().toJson(searchResponse), String.class);
            logMe("Sent SEARCHOK to " + searchResponse.getCredential().getIp() + ":" + searchResponse.getCredential().getPort() + " at " + getCurrentTime());
        } catch (ResourceAccessException exception) {
            //connection refused to the api end point
            if (node.getRoutingTable().contains(searchResponse.getCredential())) {
                node.getRoutingTable().remove(searchResponse.getCredential());
//                logMe(searchResponse.getCredential().getIp() + "node is not available and removed from routing table.");
            }
            //Todo: Remove this neighbour from stat table
        }
    }

    /**
     * @param searchResponse Called when I get a successful response from someone else for my search
     */
    @Override
    public void searchSuccess(SearchResponse searchResponse) {
        //update statTable
        int sequenceNo = searchResponse.getSequenceNo();
        QueryRecord queryRecord = null;
        for (QueryRecord qr : node.getQueryTable()) {
            if (qr.getSequenceNo() == sequenceNo) {
                queryRecord = qr;
                break;
            }
        }
        if (queryRecord != null) {
            String query = queryRecord.getSearchQuery();
            List<String> fileList = searchResponse.getFileList();

            StatRecord statRecord = new StatRecord(query, queryRecord.getTriggeredTime(), new Date(), searchResponse.getHops(), searchResponse.getCredential(), fileList);
            boolean isFileAlreadyReceived = false;
            for (StatRecord sr : node.getStatTable()) {
                if (sr.getSearchQuery().equals(statRecord.getSearchQuery()) && sr.getServedNode().equals(statRecord.getServedNode())) {
                    isFileAlreadyReceived = true;
                    break;
                }
            }
            if (!isFileAlreadyReceived) {
                node.getStatTable().add(statRecord);
                node.getDisplayTable().get(query).addAll(fileList);
            }
            logMe("\"" + statRecord.getSearchQuery() + "\" found at: " + searchResponse.getCredential().getIp() + ":" + searchResponse.getCredential().getPort() + " at " + getCurrentTime());
        }
    }


    @Override
    public void processResponse(Message response) {
        if (response instanceof RegisterResponse) {
            RegisterResponse registerResponse = (RegisterResponse) response;
            if (registerResponse.getNoOfNodes() == Constant.Codes.Register.ERROR_ALREADY_REGISTERED) {
                logMe("Already registered at Bootstrap with same username\n");
                Credential credential = node.getCredential();
                credential.setUsername(UUID.randomUUID().toString());
                node.setCredential(credential);
                register();
            } else if (registerResponse.getNoOfNodes() == Constant.Codes.Register.ERROR_DUPLICATE_IP) {
                logMe("Already registered at Bootstrap with same port\n");
                Credential credential = node.getCredential();
                credential.setPort(credential.getPort() + 1);
                node.setCredential(credential);
                register();
            } else if (registerResponse.getNoOfNodes() == Constant.Codes.Register.ERROR_CANNOT_REGISTER) {
                logMe("Can’t register. Bootstrap server full. Try again later\n");
            } else if (registerResponse.getNoOfNodes() == Constant.Codes.Register.ERROR_COMMAND) {
                logMe("Error in command");
            } else {
                List<Credential> credentialList = registerResponse.getCredentials();
                for (Credential credential : credentialList) {
                    join(credential);
                }
                System.setProperty(Constant.SERVER_PORT, String.valueOf(node.getCredential().getPort()));
                this.regOk = true;
            }

        } else if (response instanceof UnregisterResponse) {
            //TODO: set leave request for all of the nodes at routing table
            node.setRoutingTable(new ArrayList<>());
            node.setFileList(new ArrayList<>());
            node.setStatTable(new ArrayList<>());
            this.regOk = false;
        }
    }

    @Override
    public boolean isRegOk() {
        return regOk;
    }

    @Override
    public List<String> checkFilesInFileList(String fileName, List<String> fileList) {
        Pattern pattern = Pattern.compile(fileName, Pattern.CASE_INSENSITIVE + Pattern.LITERAL);
        return fileList.stream().filter(pattern.asPredicate()).collect(Collectors.toList());
    }

    @Override
    public List<StatRecord> checkFilesInStatTable(String fileName, List<StatRecord> statTable) {
        Pattern pattern = Pattern.compile(fileName);
        List<StatRecord> StatTableSearchResult = new ArrayList();
        for (StatRecord statRecord : statTable) {
            if (pattern.matcher(statRecord.getSearchQuery()).find()) {
                StatTableSearchResult.add(statRecord);
            }
        }
        return StatTableSearchResult;
    }


    /**
     * @param searchRequest Make a new search request from local node
     */
    @Override
    public void triggerSearchRequest(SearchRequest searchRequest) {
        String query = searchRequest.getFileName();
//        logMe("Sent SEARCH to others for \"" + query + "\" at " + getCurrentTime());
        node.incSearchedQueryCount();

        searchRequest.setHops(searchRequest.incHops());
        node.getQueryTable().add(new QueryRecord(searchRequest.getSequenceNo(), query, new Date()));
        List<StatRecord> StatTableSearchResult = checkFilesInStatTable(query, node.getStatTable());
        // Send search request to stat table members
        for (StatRecord statRecord : StatTableSearchResult) {
            if (statRecord.getSearchQuery().equals(searchRequest.getFileName())) {
                Credential credential = statRecord.getServedNode();
                search(searchRequest, credential);
//                logMe("Send SER request message to stat table member " + credential.getIp() + " : " + credential.getPort() + "\n");
            }
        }
        //TODO: Wait and see for stat members rather flooding whole routing table
        // Send search request to routing table members
        for (Credential credential : node.getRoutingTable()) {
            if (search(searchRequest, credential)){ break;}
        }
    }


    /**
     * Check a search request from neighbour node and pass if needed
     */
    @Override
    public void passSearchRequest(SearchRequest searchRequest) {

//        if (searchRequest.getCredential().getIp() == node.getCredential().getIp() && searchRequest.getCredential().getPort() == node.getCredential().getPort()) {
        if (searchRequest.getCredential().equals(node.getCredential())) {
            return; // search query loop has eliminated
        }
        List<String> searchResult = checkFilesInFileList(searchRequest.getFileName(), node.getFileList());
        if (!searchResult.isEmpty()) {
            SearchResponse searchResponse = new SearchResponse(searchRequest.getSequenceNo(), searchResult.size(), searchRequest.getCredential(), searchRequest.getHops(), searchResult);
            searchOk(searchResponse);
        } else {
            //logMe("File is not available at " + node.getCredential().getIp() + " : " + node.getCredential().getPort() + "\n");
            if (searchRequest.getHops() <= TTL) {
                searchRequest.setHops(searchRequest.incHops());
                List<StatRecord> StatTableSearchResult = checkFilesInStatTable(searchRequest.getFileName(), node.getStatTable());
                // Send search request to stat table members
                for (StatRecord statRecord : StatTableSearchResult) {
                    if (statRecord.getSearchQuery().equals(searchRequest.getFileName())) {
                        Credential credential = statRecord.getServedNode();
                        node.incForwardedQueryCount();
                        search(searchRequest, credential);
                    }
                }
                //TODO: Wait and see for stat members rather flooding whole routing table
                // Send search request to routing table members
                for (Credential credential : node.getRoutingTable()) {
                    node.incForwardedQueryCount();
                    if (search(searchRequest, credential)){ break;}
                }
            } else {
//                logMe("Search request from" + searchRequest.getCredential().getIp() + ":" + searchRequest.getCredential().getPort() + "is blocked by hop TTL\n");
            }
        }
    }

    @Override
    public void removeFromStatTable(Credential credential) {
        List<StatRecord> statTable = node.getStatTable();
        for (StatRecord statRecord : statTable) {
            if (credential.equals(statRecord.getServedNode())) {
                statTable.remove(statRecord);
            }
        }
    }

    @Override
    public void logMe(String log) {
        this.displayLog.add(log);
        this.logFlag = true;
        System.out.println(log);
    }

    public String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

}