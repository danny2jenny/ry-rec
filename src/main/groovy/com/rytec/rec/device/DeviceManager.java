package com.rytec.rec.device;


import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.DeviceNode;
import com.rytec.rec.util.DeviceType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by danny on 16-11-14.
 * 通过这个接口和前端设备进行通信
 * 这里会根据前端设备的类型去调用相应的Device驱动
 * <p>
 * 这是所有与设备操作的入口
 */
@Service
public class DeviceManager {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ApplicationContext context;

    @Autowired
    DbConfig dbConfig;

    static Map<Integer, Object> deviceOperators = new HashMap();

    /*
    * 两级 HashMap
    * 第一级：deviceId -> hashmap
    * 第二级：nodeId -> DeviceNode
    */
    HashMap<Integer, HashMap> deviceNodeList = new HashMap();


    /*
    * 前端设备数据变化
    * DeviceAircon: 设备id
    * fun：设备功能号
    * oldValue：原值
    * newValue：新值
    */
    public void onValueChange(int device, int fun, float oldValue, float newValue) {

    }


    /*
    * 初始化Devices
    */
    @PostConstruct
    private void init() {

        //初始化Device 和 Node 的关系

        List<DeviceNode> deviceNodeList = dbConfig.getDeviceNodeList();

        for (DeviceNode item : deviceNodeList) {

            HashMap<Integer, DeviceNode> dn = this.deviceNodeList.get(item.getId());

            if (dn == null) {
                dn = new HashMap<>();
                this.deviceNodeList.put(item.getId(), dn);
            }
            dn.put(item.getNid(), item);
        }

        //初始化 Device 的实现
        Map<String, Object> devices = context.getBeansWithAnnotation(DeviceType.class);

        for (Object device : devices.values()) {
            Class<? extends Object> deviceClass = device.getClass();
            DeviceType annotation = deviceClass.getAnnotation(DeviceType.class);
            deviceOperators.put(annotation.value(), device);
        }

    }


    //得到一个设备的实例对象
    public static BaseDevice getDevice(int type) {
        return (BaseDevice) deviceOperators.get(type);
    }


}
