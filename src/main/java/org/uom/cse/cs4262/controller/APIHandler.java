//package org.uom.cse.cs4262.controller;
//
//import com.google.gson.Gson;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.uom.cse.cs4262.api.message.request.SearchRequest;
//
///**
// * @author Chanaka Lakmal
// * @date 5/11/2017
// * @since 1.0
// */
//
//@RestController
//public class APIHandler {
//
//    @Autowired
//    private NodeOpsWS nodeOpsWS;
//
//    @RequestMapping(value = "/search", method = RequestMethod.POST)
//    @ResponseBody
//    public String root(@RequestBody String json) {
//        SearchRequest searchRequest = new Gson().fromJson(json, SearchRequest.class);
//        nodeOpsWS.triggerSearchRequest(searchRequest);
//        return "SUCCESS";
//    }
//}
