package com.rytec.rec.node;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by danny on 17-1-21.
 * Node 的基础类
 */
public class BaseNode {
    static public NodeConfig parseConfig(String inStr) {
        NodeConfig nodeConfig = new NodeConfig();
        try {
            nodeConfig = new ObjectMapper().readValue(inStr, NodeConfig.class);

        } catch (IOException e) {

        }
        return nodeConfig;
    }
}
