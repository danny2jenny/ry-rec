/**
 * Created by danny on 16-11-14.
 * 通过这个接口和前端设备进行通信
 * 这里会根据前端设备的类型去调用相应的Device驱动
 * <p>
 * 这是所有与设备操作的入口
 */


package com.rytec.rec.device;

import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.Device;
import com.rytec.rec.db.model.DeviceNode;
import com.rytec.rec.util.ConstantDeviceState;
import com.rytec.rec.util.DeviceType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    HashMap<Integer, HashMap> deviceNodeListByNode = new HashMap();


    /*
    * 两级 HashMap
    * 第一级：deviceId -> hashmap
    * 第二级：funId -> DeviceNode
    */
    HashMap<Integer, HashMap> deviceNodeListByFun = new HashMap();


    /**
     * Device 的运行时状态
     */

    HashMap<Integer, DeviceRuntimeBean> deviceRuntimeList = new HashMap();

    /*
    * 初始化Devices
    */
    @PostConstruct
    private void init() {

        //初始化Device 和 Node 的关系

        List<DeviceNode> deviceNodeList = dbConfig.getDeviceNodeList();

        for (DeviceNode item : deviceNodeList) {

            // 建立 byNode 的hash
            HashMap<Integer, DeviceNode> dnByNode = this.deviceNodeListByNode.get(item.getId());

            if (dnByNode == null) {
                dnByNode = new HashMap<>();
                this.deviceNodeListByNode.put(item.getId(), dnByNode);
            }
            dnByNode.put(item.getNid(), item);

            // 建立 byFunID 的 hash
            HashMap<Integer, DeviceNode> dnByFun = this.deviceNodeListByFun.get(item.getId());
            if (dnByFun == null) {
                dnByFun = new HashMap<>();
                this.deviceNodeListByFun.put(item.getId(), dnByFun);
            }
            dnByFun.put(item.getNfun(), item);

        }

        //初始化 Device 的实现
        Map<String, Object> deviceOperatorList = context.getBeansWithAnnotation(DeviceType.class);

        for (Object device : deviceOperatorList.values()) {
            Class<? extends Object> deviceClass = device.getClass();
            DeviceType annotation = deviceClass.getAnnotation(DeviceType.class);
            deviceOperators.put(annotation.value(), device);
        }

        // 初始化 Device 的运行时状态
        List<Device> devices = dbConfig.getDeviceList();
        for (Device item : devices) {
            DeviceRuntimeBean deviceRuntimeBean = new DeviceRuntimeBean();
            deviceRuntimeBean.device = item;
            deviceRuntimeBean.state = new DeviceStateBean();
            deviceRuntimeBean.state.device = item.getId();
            deviceRuntimeBean.state.state = ConstantDeviceState.STATE_INAVAILABLE;
            deviceRuntimeList.put(item.getId(), deviceRuntimeBean);
        }

    }

    //得到一个设备的实例对象
    public AbstractOperator getDeviceOperator(int type) {
        return (AbstractOperator) deviceOperators.get(type);
    }


    /**
     * 设备的一个功能的状态发生改变，在这里集中进行处理
     * <p>
     * 该状态改变分配道对应Device的对象进行处理
     *
     * @param device   设备id
     * @param fun      设备功能号
     * @param oldValue 原值
     * @param newValue 新值
     */
    public void onValueChange(int device, int fun, Object oldValue, Object newValue) {
        // 首先找到 Device 的实例
        DeviceNode deviceNode = (DeviceNode) deviceNodeListByFun.get(device).get(fun);
        AbstractOperator abstractOperator = getDeviceOperator(deviceNode.getDtype());

        // 在实例中处理值的改变
        abstractOperator.onValueChanged(device, fun, oldValue, newValue);
    }

    public HashMap<Integer, DeviceStateBean> getDeviceStateList() {
        HashMap<Integer, DeviceStateBean> deviceStateBeanHashMap = new HashMap();
        for (int i : deviceRuntimeList.keySet()) {
            DeviceRuntimeBean deviceRuntimeBean = deviceRuntimeList.get(i);
            deviceStateBeanHashMap.put(i, deviceRuntimeBean.state);
        }
        return deviceStateBeanHashMap;
    }
}
