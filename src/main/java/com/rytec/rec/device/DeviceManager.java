/**
 * Created by danny on 16-11-14.
 * 通过这个接口和前端设备进行通信
 * 这里会根据前端设备的类型去调用相应的Device驱动
 * <p>
 * 这是所有与设备操作的入口
 */


package com.rytec.rec.device;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.Device;
import com.rytec.rec.db.model.DeviceNode;
import com.rytec.rec.service.IEC61850Service;
import com.rytec.rec.service.iec60870.Iec60870Server;
import com.rytec.rec.util.ConstantDeviceState;
import com.rytec.rec.util.AnnotationDeviceType;
import com.rytec.rec.util.ConstantMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Order(300)
public class DeviceManager extends RecBase implements ManageableInterface {

    @Autowired
    ApplicationContext context;

    @Autowired
    DbConfig dbConfig;

    @Autowired
    IEC61850Service iec61850Service;

    @Autowired
    Iec60870Server iec60870Server;

    /*
     * device操作接口
     */
    private Map<Integer, AbstractOperator> deviceOperators = new HashMap();

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
     * <p>
     * deviceId-> runtimeBean
     */

    HashMap<Integer, DeviceRuntimeBean> deviceRuntimeList = new HashMap();

    /**
     * 通过方法访问
     *
     * @return
     */
    public HashMap<Integer, DeviceRuntimeBean> getDeviceRuntimeList() {
        return deviceRuntimeList;
    }

    // 初始化接口的索引
    private void initOperatorInterface() {
        //初始化 Device 的实现
        Map<String, Object> deviceOperatorList = context.getBeansWithAnnotation(AnnotationDeviceType.class);

        for (Object device : deviceOperatorList.values()) {
            Class<? extends Object> deviceClass = device.getClass();
            AnnotationDeviceType annotation = deviceClass.getAnnotation(AnnotationDeviceType.class);
            deviceOperators.put(annotation.value(), (AbstractOperator) device);
        }
    }

    /*
     * 初始化Devices
     */
    private void initConfig() {
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

        // 初始化 Device 的运行时状态
        List<Device> devices = dbConfig.getDeviceList();
        for (Device item : devices) {
            DeviceRuntimeBean deviceRuntimeBean = new DeviceRuntimeBean();
            deviceRuntimeBean.device = item;
            deviceRuntimeBean.runtime = new DeviceStateBean();
            deviceRuntimeBean.runtime.device = item.getId();
            deviceRuntimeBean.runtime.iconState = ConstantDeviceState.STATE_OFFLINE;

            // 为 StateBean 中的State生成对象
            AbstractOperator deviceOperator = getOperatorByDeviceType(item.getType());
            deviceRuntimeBean.runtime.state = deviceOperator.generateStateBean();

            // 生成Device的配置对象
            Object config = deviceOperator.parseConfig(item.getOpt());
            deviceRuntimeBean.config = config;


            deviceRuntimeList.put(item.getId(), deviceRuntimeBean);
        }

        debug("DeviceManager Init...");
    }

    @PostConstruct
    private void init() {
        initOperatorInterface();
    }

    //得到一个设备的实例对象
    public AbstractOperator getOperatorByDeviceType(int type) {
        return deviceOperators.get(type);
    }

    public AbstractOperator getOperatorByDeviceId(int id) {
        DeviceRuntimeBean deviceRuntimeBean = deviceRuntimeList.get(id);
        return deviceOperators.get(deviceRuntimeBean.device.getType());
    }

    //得到所有的设备实例列表
    public Map<Integer, AbstractOperator> getAllDeviceOperator() {
        return deviceOperators;
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
     * @param unit     单位
     */
    public void onValueChange(int device, int fun, Object oldValue, Object newValue, String unit) {
        // 首先找到 Device 的实例
        // 可能没有对应的Device
        HashMap<Integer, DeviceNode> devices = deviceNodeListByFun.get(device);

        if (devices == null) {
            return;
        }

        // todo，这里有问题
        if (devices.isEmpty()){
            return;
        }

        DeviceRuntimeBean deviceNode = deviceRuntimeList.get(device);
        AbstractOperator abstractOperator = getOperatorByDeviceType(deviceNode.device.getType());

        // 在实例中处理值的改变
        // 可能产生类型转换错误
        try {
            abstractOperator.onValueChanged(device, fun, oldValue, newValue, unit);
        } catch (Exception e) {
            webNotify(ConstantMessageType.WEB_NOTIFY_MSG, "设备：" + device + "功能：" + fun + "---类型转换错误。节点类型可能设置错误");
            e.printStackTrace();
            return;
        }


        // 向更新61850中的值
        DeviceRuntimeBean deviceRuntimeBean = deviceRuntimeList.get(device);
        iec61850Service.update(deviceRuntimeBean);

        // 向60870更新值
        iec60870Server.update(deviceRuntimeBean);
    }

    public void stop() {
        deviceNodeListByNode.clear();
        deviceNodeListByFun.clear();
        deviceRuntimeList.clear();
    }

    public void start() {
        initConfig();
    }


    /**
     * 通过Device和功能号得到相应的DeviceNode；
     *
     * @param device
     * @param fun
     * @return
     */
    public DeviceNode getDeviceNodeByFun(int device, int fun) {
        HashMap<Integer, DeviceNode> devices = deviceNodeListByFun.get(device);

        if (devices == null) {
            return null;
        }

        return devices.get(fun);
    }

}
