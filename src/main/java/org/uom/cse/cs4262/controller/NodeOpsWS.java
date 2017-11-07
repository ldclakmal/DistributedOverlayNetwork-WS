package org.uom.cse.cs4262.controller;

import com.google.gson.Gson;
import org.springframework.web.client.RestTemplate;
import org.uom.cse.cs4262.api.*;
import org.uom.cse.cs4262.api.message.Message;
import org.uom.cse.cs4262.api.message.request.*;
import org.uom.cse.cs4262.api.message.response.RegisterResponse;
import org.uom.cse.cs4262.api.message.response.SearchResponse;
import org.uom.cse.cs4262.api.message.response.UnregisterResponse;
import org.uom.cse.cs4262.feature.Parser;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Chanaka Lakmal
 * @date 22/10/2017
 * @since 1.0
 */

public class NodeOpsWS implements NodeOps, Runnable {

    private Node node;
    private DatagramSocket socket;
    private boolean regOk = false;

    RestTemplate restTemplate = new RestTemplate();

    public Node getNode() {
        return node;
    }

    public NodeOpsWS(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        System.out.println("Server " + this.node.getCredential().getUsername() + " created at " + this.node.getCredential().getPort() + ". Waiting for incoming data...\n");
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
        System.out.println(msg);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(node.getBootstrap().getIp()), node.getBootstrap().getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // done
    @Override
    public void unRegister() {
        UnregisterRequest unregisterRequest = new UnregisterRequest(node.getCredential());
        String msg = unregisterRequest.getMessageAsString(Constant.Command.UNREG);
        System.out.println(msg);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(node.getBootstrap().getIp()), node.getBootstrap().getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // done
    @Override
    public void join(Credential neighbourCredential) {
        JoinRequest joinRequest = new JoinRequest(node.getCredential());
        String msg = joinRequest.getMessageAsString(Constant.Command.JOIN);
        System.out.println(msg);
        String uri = Constant.HTTP + neighbourCredential.getIp() + ":" + neighbourCredential.getPort() + Constant.UrlPattern.JOIN;
        String result = restTemplate.postForObject(uri, new Gson().toJson(joinRequest), String.class);
        System.out.println(result);

        if (result.equals(Constant.Command.JOINOK))
            node.getRoutingTable().add(neighbourCredential);

        printRoutingTable(node.getRoutingTable());
    }

//    @Override
//    public void joinOk(Credential senderCredential) {
//        JoinResponse joinResponse = new JoinResponse(0, node.getCredential());
//        String msg = joinResponse.getMessageAsString(Constant.Command.JOINOK);
//        try {
//            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(senderCredential.getIp()), senderCredential.getPort()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    // done
    @Override
    public void leave() {
        LeaveRequest leaveRequest = new LeaveRequest(node.getCredential());
        String msg = leaveRequest.getMessageAsString(Constant.Command.LEAVE);
        System.out.println(msg);
        for (Credential neighbourCredential : node.getRoutingTable()) {
            String uri = Constant.HTTP + neighbourCredential.getIp() + ":" + neighbourCredential.getPort() + Constant.UrlPattern.LEAVE;
            String result = restTemplate.postForObject(uri, new Gson().toJson(leaveRequest), String.class);
            System.out.println(result);
        }
    }

    @Override
    public void removeMe(LeaveRequest leaveRequest) {
        //check my routing table to see if leaveRequest exist
        for (Credential credential : node.getRoutingTable()) {
            if (credential.equals(leaveRequest.getCredential())) {
                node.getRoutingTable().remove(credential);
                System.out.println("Removed");
                break;
            }
        }
        System.out.println("my routing table");
        printRoutingTable(node.getRoutingTable());
    }

//        @Override
//    public void leaveOk(Credential senderCredentials) {
//        LeaveResponse leaveResponse = new LeaveResponse(0);
//        String msg = leaveResponse.getMessageAsString(Constant.Command.LEAVEOK);
//        try {
//            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(senderCredentials.getIp()), senderCredentials.getPort()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    // done
    @Override
    public void search(SearchRequest searchRequest, Credential sendCredentials) {
        String msg = searchRequest.getMessageAsString(Constant.Command.SEARCH);
        System.out.println(msg);
        String uri = Constant.HTTP + sendCredentials.getIp() + ":" + sendCredentials.getPort() + Constant.UrlPattern.SEARCH;
        String result = restTemplate.postForObject(uri, new Gson().toJson(searchRequest), String.class);
        System.out.println(result);
    }

    // done
    @Override
    public void searchOk(SearchResponse searchResponse) {
        String msg = searchResponse.getMessageAsString(Constant.Command.SEARCHOK);
        System.out.println(msg);
        String uri = Constant.HTTP + searchResponse.getCredential().getIp() + ":" + searchResponse.getCredential().getPort() + Constant.UrlPattern.SEARCHOK;
        String result = restTemplate.postForObject(uri, new Gson().toJson(searchResponse), String.class);
        System.out.println(result);
    }


//    @Override
//    public void error(Credential senderCredential) {
//        ErrorResponse errorResponse = new ErrorResponse();
//        String msg = errorResponse.getMessageAsString(Constant.Command.ERROR);
//        try {
//            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(senderCredential.getIp()), senderCredential.getPort()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void processResponse(Message response) {
        if (response instanceof RegisterResponse) {
            RegisterResponse registerResponse = (RegisterResponse) response;
            if (registerResponse.getNoOfNodes() == Constant.Codes.Register.ERROR_ALREADY_REGISTERED) {
                System.out.println("Already registered at Bootstrap with same username\n");
                Credential credential = node.getCredential();
                credential.setUsername(UUID.randomUUID().toString());
                node.setCredential(credential);
                register();
            } else if (registerResponse.getNoOfNodes() == Constant.Codes.Register.ERROR_DUPLICATE_IP) {
                System.out.println("Already registered at Bootstrap with same port\n");
                Credential credential = node.getCredential();
                credential.setPort(credential.getPort() + 1);
                node.setCredential(credential);
                register();
            } else if (registerResponse.getNoOfNodes() == Constant.Codes.Register.ERROR_CANNOT_REGISTER) {
                System.out.printf("Can’t register. Bootstrap server full. Try again later\n");
            } else if (registerResponse.getNoOfNodes() == Constant.Codes.Register.ERROR_COMMAND) {
                System.out.println("Error in command");
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

//        } else if (response instanceof SearchRequest) {
//            SearchRequest searchRequest = (SearchRequest) response;
//            triggerSearchRequest(searchRequest);
//
//        } else if (response instanceof SearchResponse) {
//            SearchResponse searchResponse = (SearchResponse) response;
//            if (searchResponse.getNoOfFiles() == Constant.Codes.Search.ERROR_NODE_UNREACHABLE) {
//                System.out.println("Failure due to node unreachable\n");
//            } else if (searchResponse.getNoOfFiles() == Constant.Codes.Search.ERROR_OTHER) {
//                System.out.println("Some other error\n");
//            } else {
//                System.out.println("--------------------------------------------------------");
//                System.out.println(searchResponse.toString());
//                System.out.println("--------------------------------------------------------");
//            }
//
//        } else if (response instanceof JoinRequest) {
//            joinOk(node.getCredential());
//
//        } else if (response instanceof JoinResponse) {
//            JoinResponse joinResponse = (JoinResponse) response;
//            List<Credential> routingTable = node.getRoutingTable();
//            routingTable.add(joinResponse.getSenderCredential());
//            node.setRoutingTable(routingTable);
//
//        } else if (response instanceof LeaveRequest) {
//            LeaveRequest leaveRequest = (LeaveRequest) response;
//            List<Credential> routingTable = node.getRoutingTable();
//            routingTable.remove(leaveRequest.getCredential());
//            node.setRoutingTable(routingTable);
//
//        } else if (response instanceof LeaveResponse) {
//            //Nothing to do here
//
//        } else if (response instanceof ErrorResponse) {
//            ErrorResponse errorResponse = (ErrorResponse) response;
//            System.out.println(errorResponse.toString());
        }
    }

    @Override
    public boolean isRegOk() {
        return regOk;
    }

    @Override
    public List<String> checkFilesInFileList(String fileName, List<String> fileList) {
        Pattern pattern = Pattern.compile(fileName);
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

    @Override
    public void triggerSearchRequest(SearchRequest searchRequest) {
        System.out.println("\nTriggered search request for " + searchRequest.getFileName() + "\n");
        List<String> searchResult = checkFilesInFileList(searchRequest.getFileName(), node.getFileList());
        if (!searchResult.isEmpty()) {
            System.out.println("File is available at " + node.getCredential().getIp() + " : " + node.getCredential().getPort() + "\n");
            SearchResponse searchResponse = new SearchResponse(searchRequest.getSequenceNo(), searchResult.size(), searchRequest.getCredential(), searchRequest.getHops(), searchResult);
            if (searchRequest.getCredential().getIp() == node.getCredential().getIp() && searchRequest.getCredential().getPort() == node.getCredential().getPort()) {
                System.out.println(searchResponse.toString());
            } else {
                System.out.println("Send SEARCHOK response message\n");
                searchOk(searchResponse);
            }

        } else {
            System.out.println("File is not available at " + node.getCredential().getIp() + " : " + node.getCredential().getPort() + "\n");
            searchRequest.setHops(searchRequest.incHops());
            List<StatRecord> StatTableSearchResult = checkFilesInStatTable(searchRequest.getFileName(), node.getStatTable());
            // Send search request to stat table members
            for (StatRecord statRecord : StatTableSearchResult) {
                Credential credential = statRecord.getServedNode();
                search(searchRequest, credential);
                System.out.println("Send SER request message to " + credential.getIp() + " : " + credential.getPort() + "\n");
            }
            //TODO: Wait and see for stat members rather flooding whole routing table
            // Send search request to routing table members
            for (Credential credential : node.getRoutingTable()) {
                search(searchRequest, credential);
                System.out.println("Send SER request message to " + credential.getIp() + " : " + credential.getPort() + "\n");
            }
        }
    }

    @Override
    public void printRoutingTable(List<Credential> routingTable) {
        System.out.println("Routing table updated as :");
        System.out.println("--------------------------------------------------------");
        System.out.println("IP \t \t \t PORT");
        for (Credential credential : routingTable) {
            System.out.println(credential.getIp() + "\t" + credential.getPort());
        }
        System.out.println("--------------------------------------------------------");
    }

}