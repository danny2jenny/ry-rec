package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.rytec.rec.db.mapper.NodeRedirectMapper;
import com.rytec.rec.db.model.NodeRedirect;
import com.rytec.rec.db.model.NodeRedirectExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * NodeRedirect 的数据库接口
 */
@Controller
public class ExtNodeRedirect {

    @Autowired
    NodeRedirectMapper nodeRedirectMapper;

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<NodeRedirect> list(ExtDirectStoreReadRequest request) {
        int channelId = (Integer) request.getParams().getOrDefault("masterId", 0);

        if (channelId == 0) {
            // 返回所有的行
            return nodeRedirectMapper.selectByExample(null);
        } else {
            // 返回制定Channel的行
            NodeRedirectExample nodeRedirectExample = new NodeRedirectExample();
            nodeRedirectExample.createCriteria().andNodeEqualTo((Integer) request.getParams().get("masterId"));
            return nodeRedirectMapper.selectByExample(nodeRedirectExample);
        }

    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public NodeRedirect update(NodeRedirect nodeRedirect) {
        nodeRedirectMapper.updateByPrimaryKey(nodeRedirect);
        return nodeRedirect;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public NodeRedirect create(NodeRedirect nodeRedirect) {
        nodeRedirectMapper.insert(nodeRedirect);
        return nodeRedirect;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public NodeRedirect delete(NodeRedirect nodeRedirect) {
        nodeRedirectMapper.deleteByPrimaryKey(nodeRedirect.getId());
        return nodeRedirect;
    }
}
