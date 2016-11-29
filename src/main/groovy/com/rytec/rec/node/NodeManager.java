package com.rytec.rec.node;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by danny on 16-11-21.
 */
@Service
public class NodeManager {

    Map nodeMap = new HashMap();

    public Map getNodeMap() {
        return nodeMap;
    }

    public void setNodeMap(Map nodeMap) {
        this.nodeMap = nodeMap;
    }

}
