package com.rytec.rec.node;

import com.rytec.rec.util.NodeType;
import org.codehaus.groovy.runtime.metaclass.ConcurrentReaderHashMap;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by danny on 16-12-13.
 */
@Service
public class NodeFactory {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ApplicationContext context;

    static Map<String, Object> nodes;

    static Map<Integer, Object> nodeList = new ConcurrentReaderHashMap();

    @PostConstruct
    public void init() {
        nodes = context.getBeansWithAnnotation(NodeType.class);
        for (Object node : nodes.values()) {
            final Class<? extends Object> nodeClass = node.getClass();
            final NodeType annotation = nodeClass.getAnnotation(NodeType.class);
            nodeList.put(annotation.value(), node);
        }
    }

    public static AbstractNode getNode(int type) {
        return (AbstractNode) nodeList.get(type);
    }
}
