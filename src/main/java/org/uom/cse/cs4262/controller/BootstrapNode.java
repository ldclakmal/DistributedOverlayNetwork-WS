package org.uom.cse.cs4262.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.uom.cse.cs4262.api.Constant;
import org.uom.cse.cs4262.api.Credential;
import org.uom.cse.cs4262.ui.NodeGUI;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * @author Chanaka Lakmal
 * @date 5/11/2017
 * @since 1.0
 */

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class BootstrapNode extends SpringBootServletInitializer {

    public static void main(String[] args) {

        HashMap<String, String> paramMap = new HashMap<>();

        for (int i = 0; i < args.length; i = i + 2) {
            paramMap.put(args[i], args[i + 1]);
            System.out.println(args[i] + " : " + args[i + 1]);
        }

        System.out.println();

        String bootstrapIp = paramMap.get("-b") != null ? paramMap.get("-b") : Constant.IP_BOOTSTRAP_SERVER;
        String nodeIp = paramMap.get("-i") != null ? paramMap.get("-i") : Constant.IP_BOOTSTRAP_SERVER;
        int nodePort = paramMap.get("-p") != null ? Integer.parseInt(paramMap.get("-p")) : new Random().nextInt(Constant.MAX_PORT_NODE - Constant.MIN_PORT_NODE) + Constant.MIN_PORT_NODE;
        String nodeUsername = paramMap.get("-u") != null ? paramMap.get("-u") : UUID.randomUUID().toString();

        Credential bootstrapServerCredential = new Credential(bootstrapIp, Constant.PORT_BOOTSTRAP_SERVER, Constant.USERNAME_BOOTSTRAP_SERVER);

        // Generate self credentials
        Credential nodeCredential = new Credential(nodeIp, nodePort, nodeUsername);

        // Initiate the thread for UDP connection
        NodeOpsWS nodeOpsWS = new NodeOpsWS(bootstrapServerCredential, nodeCredential);

        // Register in network
        nodeOpsWS.register();

        while (true) {
            try {
                Thread.sleep(1000);
                if (nodeOpsWS.isRegOk()) {
                    nodeOpsWS.getNodeThread().stop();
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            NodeGUI nodeGUI = new NodeGUI(nodeOpsWS);
            nodeGUI.start();
        });

        SpringApplication.run(BootstrapNode.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BootstrapNode.class);
    }
}