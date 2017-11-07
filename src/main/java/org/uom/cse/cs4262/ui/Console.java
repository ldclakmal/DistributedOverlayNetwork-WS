//package org.uom.cse.cs4262.ui;
//
//import org.uom.cse.cs4262.api.message.request.SearchRequest;
//import org.uom.cse.cs4262.controller.NodeOpsWS;
//
//public abstract class Console {
//
//    NodeOpsWS nodeOpsWS;
//    int sequenceNo;
//
//    public Console(NodeOpsWS nodeOpsWS) {
//        this.nodeOpsWS = nodeOpsWS;
//    }
//
//    void search(String query) {
//        nodeOpsWS.search(new SearchRequest(++sequenceNo, nodeOpsWS.getNode().getCredential(), query, 0), nodeOpsWS.getNode().getCredential());
//    }
//
//    void leave() {
//        nodeOpsWS.leave();
//    }
//
//    void unregister() {
//        nodeOpsWS.unRegister();
//    }
//
//    abstract public void start();
//}