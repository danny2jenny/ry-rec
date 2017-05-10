package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.rytec.rec.db.mapper.PanoramaDeviceMapper;
import com.rytec.rec.db.model.PanoramaDevice;
import com.rytec.rec.db.model.PanoramaDeviceExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by danny on 17-5-10.
 */

@Controller
public class ExtPanoramaDevice {

    @Autowired
    PanoramaDeviceMapper panoramaDeviceMapper;

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<PanoramaDevice> list(ExtDirectStoreReadRequest request) {
        PanoramaDeviceExample panoramaDeviceExample = new PanoramaDeviceExample();
        panoramaDeviceExample.createCriteria().andSceneEqualTo((Integer) request.getParams().get("pId"));
        return panoramaDeviceMapper.selectByExample(panoramaDeviceExample);
    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public PanoramaDevice update(PanoramaDevice panoramaDevice) {
        panoramaDeviceMapper.updateByPrimaryKey(panoramaDevice);
        return panoramaDevice;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public PanoramaDevice create(PanoramaDevice panoramaDevice) {
        panoramaDeviceMapper.insert(panoramaDevice);
        return panoramaDevice;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public PanoramaDevice delete(PanoramaDevice panoramaDevice) {
        panoramaDeviceMapper.deleteByPrimaryKey(panoramaDevice.getId());
        return panoramaDevice;
    }
}
