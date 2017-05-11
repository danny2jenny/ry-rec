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

    /**
     * 添加新的PanoramaDevice
     *
     * @param device
     * @param scene
     * @param pitch
     * @param yaw
     * @return
     */
    @ExtDirectMethod
    public PanoramaDevice savePanoramaDevice(int device, int scene, float pitch, float yaw) {
        PanoramaDevice panoramaDevice = new PanoramaDevice();
        panoramaDevice.setDevice(device);
        panoramaDevice.setScene(scene);
        panoramaDevice.setPitch(pitch);
        panoramaDevice.setYaw(yaw);
        panoramaDeviceMapper.insert(panoramaDevice);
        return panoramaDevice;
    }

    /**
     * 得到一个scene的device
     *
     * @param scene
     * @return
     */
    @ExtDirectMethod
    public List<PanoramaDevice> getPanoramaDevices(int scene) {
        PanoramaDeviceExample panoramaDeviceExample = new PanoramaDeviceExample();
        panoramaDeviceExample.createCriteria().andSceneEqualTo(scene);
        return panoramaDeviceMapper.selectByExample(panoramaDeviceExample);
    }

    @ExtDirectMethod
    public void delPanoramaDevices(int id) {
        panoramaDeviceMapper.deleteByPrimaryKey(id);
    }

}
