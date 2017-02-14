package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.rytec.rec.db.mapper.NodeMapper;
import com.rytec.rec.db.model.Node;
import com.rytec.rec.db.model.NodeExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by danny on 16-12-20.
 */
@Controller
public class ExtNode {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NodeMapper nodeMapper;

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<Node> list(ExtDirectStoreReadRequest request) {
        NodeExample nodeExample = new NodeExample();
        nodeExample.createCriteria().andCidEqualTo((Integer) request.getParams().get("masterId"));
        return nodeMapper.selectByExample(nodeExample);
    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Node update(Node node) {
        nodeMapper.updateByPrimaryKey(node);
        return node;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Node create(Node node) {
        nodeMapper.insert(node);
        return node;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Node delete(Node node) {
        nodeMapper.deleteByPrimaryKey(node.getId());
        return node;
    }

}
