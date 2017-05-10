package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.rytec.rec.db.mapper.PanoramaMapper;
import com.rytec.rec.db.model.Panorama;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by danny on 17-5-10.
 */

@Controller
public class ExtPanorama {

    @Autowired
    PanoramaMapper panoramaMapper;

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<Panorama> list(ExtDirectStoreReadRequest request) {
        return panoramaMapper.selectByExample(null);
    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Panorama update(Panorama node) {
        panoramaMapper.updateByPrimaryKey(node);
        return node;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Panorama create(Panorama panorama) {
        panoramaMapper.insert(panorama);
        return panorama;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Panorama delete(Panorama gisLayer) {
        panoramaMapper.deleteByPrimaryKey(gisLayer.getId());

        // 删除文件
        return gisLayer;
    }
}
