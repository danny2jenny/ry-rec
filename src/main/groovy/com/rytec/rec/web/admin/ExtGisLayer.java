package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.rytec.rec.db.mapper.GisLayerMapper;
import com.rytec.rec.db.model.GisLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by danny on 17-1-6.
 */
@Controller
public class ExtGisLayer {

    @Autowired
    GisLayerMapper gisLayerMapper;

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<GisLayer> list(ExtDirectStoreReadRequest request) {
        return gisLayerMapper.selectByExample(null);
    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public GisLayer update(GisLayer node) {
        gisLayerMapper.updateByPrimaryKey(node);
        return node;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public GisLayer create(GisLayer gisLayer) {
        gisLayerMapper.insert(gisLayer);
        return gisLayer;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public GisLayer delete(GisLayer gisLayer) {
        gisLayerMapper.deleteByPrimaryKey(gisLayer.getId());

        // 删除文件
        return gisLayer;
    }
}
