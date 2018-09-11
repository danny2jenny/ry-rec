package com.rytec.rec.service.iec60870;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.Config;
import com.rytec.rec.db.model.DeviceGis;
import com.rytec.rec.db.model.GisLayer;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.service.heshen.C_DataType;
import com.rytec.rec.service.heshen.C_FunType;
import com.rytec.rec.service.heshen.json.ConfigDevice;
import com.rytec.rec.service.heshen.json.ConfigFunction;
import com.rytec.rec.service.heshen.json.ConfigMaster;
import com.rytec.rec.util.Tools;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * 60870 接口的相关文件
 * <p>
 * 1、生成XML配置文件
 * 2、根据index返回文件的相关信息
 */
@Service
@Order(400)
public class FileManager extends RecBase implements ManageableInterface {

    @Autowired
    DeviceManager deviceManager;

    @Autowired
    DbConfig dbConfig;

    @Autowired
    AddrConvert addrConvert;

    //文件上传的路径
    String xmlFileName;

    /**
     * 图标类型到config的Hash
     * value -> config
     */
    public HashMap<Integer, Config> iconConfigMap = new HashMap<>();

    // 设备对应的GIS  deviceId-> deviceGis
    public HashMap<Integer, DeviceGis> deviceGisHashMap = new HashMap<>();

    // GIS 图层
    public HashMap<Integer, Integer> gisLayerMap = new HashMap<>();

    @Override
    public void stop() {
        iconConfigMap.clear();
        deviceGisHashMap.clear();
        gisLayerMap.clear();
    }

    @Override
    public void start() {
        xmlFileName = dbConfig.getCfg("iec60870.xml");
        stop();
        getConfig();
        genXML();
    }

    /**
     * 得到配置文件，通过配置文件，获取设备类型和子类型
     */
    private void getConfig() {
        List<Config> configList = dbConfig.getConfigList();
        for (Config configItem : configList) {
            if (configItem.getCat() == 11) {
                iconConfigMap.put(Integer.parseInt(configItem.getValue()), configItem);
            }
        }

        List<DeviceGis> deviceGisList = dbConfig.getDeviceGisList();
        for (DeviceGis deviceGis : deviceGisList) {

            if (deviceGis.getGtype() == null) {
                continue;
            }

            if (deviceGis.getGtype() == 1) {
                deviceGisHashMap.put(deviceGis.getDid(), deviceGis);
            }
        }

        List<GisLayer> gisLayerList = dbConfig.getGisLayerList();
        for (GisLayer gisLayer : gisLayerList) {
            gisLayerMap.put(gisLayer.getId(), gisLayer.getZoom());
        }
    }

    /**
     * 生成Json的台账信息
     */
    public String genJson() {

        ConfigMaster jsonCfg = new ConfigMaster();

        // 台账文件结构
        jsonCfg.id = Integer.parseInt(dbConfig.getCfg("iec60870.addr"));
        jsonCfg.name = dbConfig.getCfg("project.name");
        jsonCfg.vender = dbConfig.getCfg("project.vender");
        jsonCfg.createtime = Tools.currentTimeIsoStr();
        jsonCfg.ip = dbConfig.getCfg("heshen104.ip");
        jsonCfg.port = dbConfig.getCfg("heshen104.port");

        // 添加设备
        for (DeviceRuntimeBean deviceRuntimeBean : deviceManager.getDeviceRuntimeList().values()) {
            // 处理没有图标的情况
            if (deviceRuntimeBean.device.getIcon() == 0) {
                continue;
            }

            // 301 空调； 401 摄像头； 9999 全景
            if (deviceRuntimeBean.device.getType() == 301 | deviceRuntimeBean.device.getType() == 401 | deviceRuntimeBean.device.getType() == 9999) {
                continue;
            }

            ConfigDevice device = new ConfigDevice();
            jsonCfg.devices.add(device);

            // 对 device 进行填充
            device.devName = deviceRuntimeBean.device.getName();
            device.devType = iconConfigMap.get(deviceRuntimeBean.device.getIcon()).getType();
            DeviceGis deviceGis = deviceGisHashMap.get(deviceRuntimeBean.device.getId());
            if (deviceGis != null) {
                device.area = gisLayerMap.get(deviceGis.getLayer());
                String posStr = deviceGis.getData().substring(1, deviceGis.getData().length() - 1);
                int commaPos = posStr.indexOf(',');
                device.longitude = posStr.substring(0, commaPos - 1);
                device.latitude = posStr.substring(commaPos, posStr.length());
            }

            ConfigFunction configFunction = new ConfigFunction();
            switch (deviceRuntimeBean.device.getType()) {
                case 201:           // 模拟量
                    configFunction.funcName = deviceRuntimeBean.device.getName();
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_MEASURE;
                    configFunction.addr = "0x" + Integer.toHexString(deviceRuntimeBean.device.getId() + C_DeviceAddr.MEASURE_ADDR);
                    configFunction.dataType = C_DataType.S_FLOAT;
                    device.function.add(configFunction);
                    break;
                case 102:           // 开关输入
                    configFunction.funcName = deviceRuntimeBean.device.getName();
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_STATE;
                    configFunction.addr = "0x" + Integer.toHexString(deviceRuntimeBean.device.getId() + C_DeviceAddr.STATE_ADDR);
                    configFunction.dataType = C_DataType.SWITCH;
                    device.function.add(configFunction);
                    break;
                case 101:           // 开关控制
                    // 控制
                    configFunction.funcName = "(控制)";
                    configFunction.funcType = C_FunType.CONTROL;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_CONTROL;
                    configFunction.addr = "0x" + Integer.toHexString(deviceRuntimeBean.device.getId() + C_DeviceAddr.CONTROL_ADDR);
                    configFunction.dataType = C_DataType.SWITCH;
                    device.function.add(configFunction);

                    // 状态
                    configFunction = new ConfigFunction();
                    configFunction.funcName = "(状态)";
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_STATE;
                    configFunction.addr = "0x" + Integer.toHexString(deviceRuntimeBean.device.getId() + C_DeviceAddr.RUN_STATE);
                    configFunction.dataType = C_DataType.SWITCH;
                    device.function.add(configFunction);

                    // 远程就地
                    configFunction = new ConfigFunction();
                    configFunction.funcName = "(模式)";
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_STATE;
                    configFunction.addr = "0x" + Integer.toHexString(deviceRuntimeBean.device.getId() + C_DeviceAddr.RUN_MODE);
                    configFunction.dataType = C_DataType.SWITCH;
                    device.function.add(configFunction);
                    break;
                case 501:           // 电流传感（环流）
                    // A相
                    configFunction.funcName = "(A相)";
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_MEASURE;
                    configFunction.addr = "0x" + Integer.toHexString(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId()));
                    configFunction.dataType = C_DataType.S_FLOAT;
                    device.function.add(configFunction);

                    // B相
                    configFunction = new ConfigFunction();
                    configFunction.funcName = "(B相)";
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_MEASURE;
                    configFunction.addr = "0x" + Integer.toHexString(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId() + 1));
                    configFunction.dataType = C_DataType.S_FLOAT;
                    device.function.add(configFunction);

                    // C相
                    configFunction = new ConfigFunction();
                    configFunction.funcName = "(B相)";
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_MEASURE;
                    configFunction.addr = "0x" + Integer.toHexString(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId() + 2));
                    configFunction.dataType = C_DataType.S_FLOAT;
                    device.function.add(configFunction);

                    // 0序
                    configFunction = new ConfigFunction();
                    configFunction.funcName = "(0序)";
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_MEASURE;
                    configFunction.addr = "0x" + Integer.toHexString(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId() + 3));
                    configFunction.dataType = C_DataType.S_FLOAT;
                    device.function.add(configFunction);
                    break;
                case 502:           // 光纤测温告警
                    // 告警类型
                    configFunction.funcName = "告警类型";
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_MEASURE;
                    configFunction.addr = "0x" + Integer.toHexString(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId()));
                    configFunction.dataType = C_DataType.UNSIGN_16;
                    device.function.add(configFunction);

                    // 告警位置
                    configFunction = new ConfigFunction();
                    configFunction.funcName = "告警位置";
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_MEASURE;
                    configFunction.addr = "0x" + Integer.toHexString(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId() + 1));
                    configFunction.dataType = C_DataType.UNSIGN_16;
                    device.function.add(configFunction);

                    // 告警值
                    configFunction = new ConfigFunction();
                    configFunction.funcName = "告警值";
                    configFunction.funcType = C_FunType.DATA;
                    configFunction.addrType = C_DeviceType.ADDR_TYPE_MEASURE;
                    configFunction.addr = "0x" + Integer.toHexString(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId() + 2));
                    configFunction.dataType = C_DataType.S_FLOAT;
                    device.function.add(configFunction);

                    break;
            }
        }

        // 生成 Json
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonCfg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * XML
     * Device段
     * Name: 设备名称，对应Device表中的Name
     * DeviceType：属性表示设备的类型，对应Config中图标中的type
     * Map：地图的地图名称
     * Position：坐标，经度，维度
     * <p>
     * Function 子段
     * Name：功能名
     * MsgType：功能号，这个需要特别的处理，相当于功能号
     * AddrType：
     * 1：遥信
     * 2：遥测
     * 3：遥控
     * 4：遥调
     * 5：文件类型
     * Addr：地址，应该对应我们的端口
     */
    private void genXML() {

        // 创建XML文档树
        Document document = DocumentHelper.createDocument();
        // 创建根节点items
        Element rootElement = document.addElement("Site");
        rootElement.addAttribute("SName", dbConfig.getCfg("project.name"));
        rootElement.addAttribute("SAddr", dbConfig.getCfg("iec60870.addr"));


        // 创建根节点下的item子节点
        Element subElement = rootElement.addElement("Productor");
        subElement.setText(dbConfig.getCfg("project.vender"));

        /**
         * 生成Device的结构
         */
        Element device, name, map, type, target, position, function, msgType, addrType, addr;
        for (DeviceRuntimeBean deviceRuntimeBean : deviceManager.getDeviceRuntimeList().values()) {
            // 处理没有图标的情况
            if (deviceRuntimeBean.device.getIcon() == 0) {
                continue;
            }

            if (deviceRuntimeBean.device.getType() == 301 | deviceRuntimeBean.device.getType() == 401 | deviceRuntimeBean.device.getType() == 9999) {
                continue;
            }

            device = rootElement.addElement("Device");

            name = device.addElement("Name");
            name.setText(deviceRuntimeBean.device.getName());

            // 得到地图的图层和坐标
            DeviceGis deviceGis = deviceGisHashMap.get(deviceRuntimeBean.device.getId());
            if (deviceGis != null) {
                map = device.addElement("Map");
                map.setText(deviceGis.getLayer() + ".jpg");

                position = device.addElement("Position");
                position.setText(deviceGis.getData().substring(1, deviceGis.getData().length() - 1));

                target = device.addElement("Target");
                target.setText(gisLayerMap.get(deviceGis.getLayer()).toString());
            }


            type = device.addElement("Type");
            type.setText(iconConfigMap.get(deviceRuntimeBean.device.getIcon()).getType().toString());


            // todo 生成 Function 段

            /**
             * 首先是DeviceType，然后对应Config中Icon确定子类别
             * MsgType 定义
             * 1~100: 功能端口
             * 101-200： 其他端口
             * 201-300: 故障端口
             */
            switch (deviceRuntimeBean.device.getType()) {
                case 201:           // 模拟量
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText(iconConfigMap.get(deviceRuntimeBean.device.getIcon()).getName());

                    msgType = function.addElement("MsgType");
                    msgType.setText(iconConfigMap.get(deviceRuntimeBean.device.getIcon()).getFun().toString());

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_MEASURE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(deviceRuntimeBean.device.getId() + C_DeviceAddr.MEASURE_ADDR));
                    break;
                case 102:           // 开关输入
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("报警信息");

                    msgType = function.addElement("MsgType");
                    msgType.setText(iconConfigMap.get(deviceRuntimeBean.device.getIcon()).getFun().toString());

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_STATE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(deviceRuntimeBean.device.getId() + C_DeviceAddr.STATE_ADDR));
                    break;
                case 101:           // 开关控制

                    // 控制功能
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("控制");

                    msgType = function.addElement("MsgType");
                    msgType.setText("201");

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_CONTROL));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(deviceRuntimeBean.device.getId() + C_DeviceAddr.CONTROL_ADDR));

                    // 状态功能
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("运行状态");

                    msgType = function.addElement("MsgType");
                    msgType.setText("1");

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_STATE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(deviceRuntimeBean.device.getId() + C_DeviceAddr.RUN_STATE));

                    // 远程就地
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("运行模式");

                    msgType = function.addElement("MsgType");
                    msgType.setText("2");

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_STATE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(deviceRuntimeBean.device.getId() + C_DeviceAddr.RUN_MODE));

                    break;
                case 501:                   // 电流传感（环流）
                    // A 相
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("A相电流");

                    msgType = function.addElement("MsgType");
                    msgType.setText("102");

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_MEASURE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId())));
                    // B 相
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("B相电流");

                    msgType = function.addElement("MsgType");
                    msgType.setText("103");

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_MEASURE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId()) + 1));
                    // C 相
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("C相电流");

                    msgType = function.addElement("MsgType");
                    msgType.setText("104");

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_MEASURE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId()) + 2));
                    // 0 相
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("O序电流");

                    msgType = function.addElement("MsgType");
                    msgType.setText("101");

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_MEASURE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId()) + 3));
                    break;
                case 502:                   // 光纤测温告警
                    // 告警类型
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("告警类型");

                    msgType = function.addElement("MsgType");
                    msgType.setText("104");

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_MEASURE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId())));
                    // 告警位置
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("告警位置");

                    msgType = function.addElement("MsgType");
                    msgType.setText("105");

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_MEASURE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId()) + 1));
                    // 告警值
                    function = device.addElement("Function");

                    name = function.addElement("Name");
                    name.setText("告警值");

                    msgType = function.addElement("MsgType");
                    msgType.setText("106");

                    addrType = function.addElement("AddrType");
                    addrType.setText(String.valueOf(C_DeviceType.ADDR_TYPE_MEASURE));

                    addr = function.addElement("Addr");
                    addr.setText(String.valueOf(addrConvert.getBase104Addr(deviceRuntimeBean.device.getId()) + 2));
                    break;
            }

        }

        /**
         * TODO：生成 配置文件，GIS图片文件Node
         */

        // 设置XML文档格式
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding("UTF-8");              //文件编码
        outputFormat.setSuppressDeclaration(false);     //是否生产xml头
        outputFormat.setIndent(true);                   //设置是否缩进
        outputFormat.setIndent("    ");                 //以四个空格方式实现缩进
        outputFormat.setNewlines(true);                 //设置是否换行

        // 转换成XML进行存储
        XMLWriter xmlWriter;
        try {
            xmlWriter = new XMLWriter(new FileOutputStream(xmlFileName), outputFormat);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * 根据 index 返回文件信息
     *
     * @param index
     * @return
     */
    public FileInfo getFileInfo(int index) {
        FileInfo fileInfo = null;
        File file;
        if (index == 0) {
            file = new File(xmlFileName);
            if (file.exists()) {
                fileInfo = new FileInfo();

                fileInfo.index = 0;
                fileInfo.fullName = xmlFileName;
                fileInfo.name = file.getName();
                fileInfo.size = (int) file.length();
                fileInfo.modifyTime = file.lastModified();
            }
        }
        return fileInfo;
    }
}
