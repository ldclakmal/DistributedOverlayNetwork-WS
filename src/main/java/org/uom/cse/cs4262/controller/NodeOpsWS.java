package org.uom.cse.cs4262.controller;

import com.google.gson.Gson;
import org.uom.cse.cs4262.api.Constant;
import org.uom.cse.cs4262.api.Credential;
import org.uom.cse.cs4262.api.Node;
import org.uom.cse.cs4262.api.NodeOps;
import org.uom.cse.cs4262.api.message.Message;
import org.uom.cse.cs4262.api.message.request.*;
import org.uom.cse.cs4262.api.message.response.*;
import org.uom.cse.cs4262.feature.Parser;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Chanaka Lakmal
 * @date 22/10/2017
 * @since 1.0
 */

public class NodeOpsWS implements NodeOps, Runnable {


    @Override
    public void run() {

    }

    @Override
    public void start() {

    }

    @Override
    public void register() {

    }

    @Override
    public void unRegister() {

    }

    @Override
    public void join(Credential neighbourCredential) {

    }

    @Override
    public void joinOk(Credential senderCredential) {

    }

    @Override
    public void leave() {

    }

    @Override
    public void leaveOk(Credential senderCredential) {

    }

    @Override
    public void search(SearchRequest searchRequest, Credential sendCredential) {

    }

    @Override
    public void searchOk(SearchResponse searchResponse) {

    }

    @Override
    public List<String> createFileList() {
        return null;
    }

    @Override
    public void processResponse(Message response) {

    }

    @Override
    public void error(Credential senderCredential) {

    }

    @Override
    public boolean isRegOk() {
        return false;
    }

    @Override
    public List<String> checkForFiles(String fileName, List<String> fileList) {
        return null;
    }

    @Override
    public void triggerSearchRequest(SearchRequest searchRequest) {

    }

    @Override
    public void printRoutingTable(List<Credential> routingTable) {

    }

    @Override
    public String callAPI(String ip, int port, String pattern, String method, String body) {
        return null;
    }
}
