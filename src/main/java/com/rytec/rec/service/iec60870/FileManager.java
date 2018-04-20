package com.rytec.rec.service.iec60870;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.db.DbConfig;
import com.rytec.rec.db.model.Config;
import com.rytec.rec.db.model.DeviceGis;
import com.rytec.rec.db.model.GisLayer;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.device.DeviceRuntimeBean;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    //文件上传的路径
    @Value("${iec60870.xml}")
    String xmlFileName;

    @Value("${project.name}")
    private String projectName;
    @Value("${project.addr}")
    private String projectAddr;
    @Value("${project.company}")
    private String projectCompany;
    @Value("${iec60870.addr}")
    public int iec60870Addr;            // 60870 地址

    /**
     * 图标类型到config的Hash
     * value -> config
     */
    public HashMap<Integer, Config> iconConfigMap = new HashMap<>();

    // 设备对应的GIS  deviceId-> deviceGis
    public HashMap<Integer, DeviceGis> deviceGisHashMap = new HashMap<>();

    // GIS 图层
    public HashMap<Integer, String> gisLayerMap = new HashMap<>();

    @Override
    public void stop() {
        iconConfigMap.clear();
        deviceGisHashMap.clear();
        gisLayerMap.clear();
    }

    @Override
    public void start() {
        getConfig();
        init();
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
            gisLayerMap.put(gisLayer.getId(), gisLayer.getFile());
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
    private void init() {

        // 创建XML文档树
        Document document = DocumentHelper.createDocument();
        // 创建根节点items
        Element rootElement = document.addElement("Site");
        rootElement.addAttribute("SName", projectName);
        rootElement.addAttribute("SAddr", String.valueOf(iec60870Addr));


        // 创建根节点下的item子节点
        Element subElement = rootElement.addElement("Productor");
        subElement.setText(projectCompany);

        /**
         * 生成Device的结构
         */
        Element device, name, map, type, position, function, msgType, addrType, addr;
        for (DeviceRuntimeBean deviceRuntimeBean : deviceManager.getDeviceRuntimeList().values()) {

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
