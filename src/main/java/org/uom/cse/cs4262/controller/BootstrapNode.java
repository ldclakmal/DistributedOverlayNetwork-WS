package org.uom.cse.cs4262.controller;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.uom.cse.cs4262.api.Constant;
import org.uom.cse.cs4262.api.Credential;
import org.uom.cse.cs4262.api.Node;
import org.uom.cse.cs4262.api.message.request.JoinRequest;
import org.uom.cse.cs4262.api.message.request.LeaveRequest;
import org.uom.cse.cs4262.api.message.request.SearchRequest;
import org.uom.cse.cs4262.api.message.response.SearchResponse;
import org.uom.cse.cs4262.ui.MainUI;
import org.uom.cse.cs4262.ui.NodeGUI;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Chanaka Lakmal
 * @date 5/11/2017
 * @since 1.0
 */

@Configuration
@ComponentScan("org.uom.cse.cs4262")
@EnableAutoConfiguration
@RestController
public class BootstrapNode extends SpringBootServletInitializer {

    private static NodeOpsWS nodeOpsWS;

    public static void main(String[] args) {

        HashMap<String, String> paramMap = new HashMap<>();

        for (int i = 0; i < args.length; i = i + 2) {
            paramMap.put(args[i], args[i + 1]);
            System.out.println(args[i] + " : " + args[i + 1]);
        }

        System.out.println();

        String bootstrapIp = paramMap.get("-b") != null ? paramMap.get("-b") : Constant.BOOTSTRAP_SERVER_IP;
        String nodeIp = paramMap.get("-i") != null ? paramMap.get("-i") : Constant.BOOTSTRAP_SERVER_IP;
        int nodePort = paramMap.get("-p") != null ? Integer.parseInt(paramMap.get("-p")) : new Random().nextInt(Constant.MAX_PORT_NODE - Constant.MIN_PORT_NODE) + Constant.MIN_PORT_NODE;
        String nodeUsername = paramMap.get("-u") != null ? paramMap.get("-u") : UUID.randomUUID().toString();

        System.setProperty(Constant.SERVER_PORT, String.valueOf(nodePort));

        Credential bootstrapCredential = new Credential(bootstrapIp, Constant.BOOTSTRAP_SERVER_PORT, Constant.BOOTSTRAP_SERVER_USERNAME);
        Credential nodeCredential = new Credential(nodeIp, nodePort, nodeUsername);

        Node node = new Node(nodeCredential, createFileList(), new ArrayList<>(), new ArrayList<>(), bootstrapCredential, 0, 0, 0, 0, new HashMap<>());

        nodeOpsWS = new NodeOpsWS(node);
        nodeOpsWS.start();
        nodeOpsWS.register();

        while (true) {
            try {
                Thread.sleep(1000);
                if (nodeOpsWS.isRegOk()) {
                    // TODO: stop node socket which is listening
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            MainUI mainFrame = new MainUI(nodeOpsWS);
            mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            mainFrame.pack();
            mainFrame.setTitle("Distributed Systems Client App");
            mainFrame.setLocationRelativeTo(null);
            mainFrame.start();
        });

        SpringApplication.run(BootstrapNode.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BootstrapNode.class);
    }

    private static List<String> createFileList() {
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("Adventures_of_Tintin");
        fileList.add("Jack_and_Jill");
        fileList.add("Glee");
        fileList.add("The_Vampire Diarie");
        fileList.add("King_Arthur");
        fileList.add("Windows_XP");
        fileList.add("Harry_Potter");
        fileList.add("Kung_Fu_Panda");
        fileList.add("Lady_Gaga");
        fileList.add("Twilight");
        fileList.add("Windows_8");
        fileList.add("Mission_Impossible");
        fileList.add("Turn_Up_The_Music");
        fileList.add("Super_Mario");
        fileList.add("American_Pickers");
        fileList.add("Microsoft_Office_2010");
        fileList.add("Happy_Feet");
        fileList.add("Modern_Family");
        fileList.add("American_Idol");
        fileList.add("Hacking_for_Dummies");
        Collections.shuffle(fileList);
        List<String> subFileList = fileList.subList(0, 5);
        System.out.println("File List : " + Arrays.toString(subFileList.toArray()) + "\n");
        return subFileList;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(@RequestBody String json) {
        System.out.println("Search api end point triggered");
        SearchRequest searchRequest = new Gson().fromJson(json, SearchRequest.class);
//        new Thread(() -> nodeOpsWS.triggerSearchRequest(searchRequest));
        Executors.newScheduledThreadPool(1).schedule(
                () -> nodeOpsWS.triggerSearchRequest(searchRequest),
                10, TimeUnit.MILLISECONDS
        );
        System.out.println("End of search aPi");
        return String.valueOf(HttpStatus.OK);
    }

    @RequestMapping(value = "/searchok", method = RequestMethod.POST)
    @ResponseBody
    public String searchok(@RequestBody String json) {
        System.out.println("Search OK api end point triggered");
        SearchResponse searchResponse = new Gson().fromJson(json, SearchResponse.class);
        nodeOpsWS.searchSuccess(searchResponse);
        return String.valueOf(HttpStatus.OK);
    }

    @RequestMapping(value = "/join", method = RequestMethod.POST)
    @ResponseBody
    public String join(@RequestBody String json) {
        System.out.println("Join api end point triggered");
        JoinRequest joinRequest = new Gson().fromJson(json, JoinRequest.class);
        nodeOpsWS.joinMe(joinRequest);
        return Constant.Command.JOINOK;
    }

    // remove me
    @RequestMapping(value = "/leave", method = RequestMethod.POST)
    @ResponseBody
    public String leave(@RequestBody String json) {
        System.out.println("Leave api end point triggered");
        LeaveRequest leaveRequest = new Gson().fromJson(json, LeaveRequest.class);
        nodeOpsWS.removeMe(leaveRequest);
        return Constant.Command.LEAVEOK;
    }
}