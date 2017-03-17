package com.rytec.rec.web.app;

import com.rytec.rec.channel.ChannelInterface;
import com.rytec.rec.channel.ChannelManager;
import com.rytec.rec.db.mapper.ConfigMapper;
import com.rytec.rec.db.model.Config;
import com.rytec.rec.db.model.ConfigExample;
import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.node.NodeInterface;
import com.rytec.rec.node.NodeManager;
import com.rytec.rec.util.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by danny on 17-1-31.
 * <p>
 * 输出JavaScript的常量数组
 * <p>
 * 如果是接口定义的常量，在impliments中加入该接口。
 * 接口需要使用：
 *
 * @JSExport("设备状态")
 * @Description("DEVICE_STATE") 这样的方式进行修饰
 */

@Controller
public class JSConfig implements ConstantDeviceFunction, ConstantDeviceState, ConstantErrorCode {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DeviceManager deviceManager;

    @Autowired
    ChannelManager channelManager;

    @Autowired
    NodeManager nodeManager;

    @Autowired
    ConfigMapper configMapper;

    public static String encodeStr(String str) {
        try {
            return new String(str.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 客户端JS，包含常量的定义
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/config")
    public String genJsConfig(Model model) {

        /**
         * 把WEB端所需要的常量输出
         * 常量在该对象的实现的接口当中
         */
        Class<?>[] constSets = this.getClass().getInterfaces();

        HashMap<String, Object> rst = new HashMap<>();

        for (Class<?> constant : constSets) {
            AnnotationJSExport annotation = constant.getAnnotation(AnnotationJSExport.class);
            HashMap<Integer, String> jsConst = new HashMap<>();
            try {
                for (Field field : constant.getDeclaredFields()) {
                    // todo: 最好生成常量名
                    String name = field.getName();
                    int value = field.getInt(null);
                    jsConst.put(value, field.getAnnotation(AnnotationJSExport.class).value());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            rst.put(annotation.value(), jsConst);
        }


        /**
         * 图标集的配置常量
         */
        ConfigExample configExample = new ConfigExample();
        configExample.createCriteria().andCatEqualTo(11);
        List<Config> icons = configMapper.selectByExample(configExample);
        HashMap<Integer, String> cicon = new HashMap<>();
        for (Config item : icons) {
            cicon.put(Integer.parseInt(item.getValue()), item.getName());
        }
        rst.put("DEVICE_ICON", cicon);

        /**
         * 把 Channel、Node、Device 的类型进行常量的输出
         *
         */

        // 得到Channel的类型常量
        HashMap<Integer, ChannelInterface> cis = channelManager.getAllChannelInterface();

        HashMap<Integer, String> ccm = new HashMap<>();
        for (Integer i : cis.keySet()) {
            ChannelInterface ci = cis.get(i);
            ccm.put(i, ci.getClass().getAnnotation(AnnotationJSExport.class).value());
        }
        rst.put("CHANNEL_TYPE", ccm);

        // Node 常量
        Map<Integer, NodeInterface> nis = nodeManager.getAllNodeInterface();
        HashMap<Integer, String> cnm = new HashMap<>();
        for (Integer i : nis.keySet()) {
            NodeInterface ni = nis.get(i);
            cnm.put(i, ni.getClass().getAnnotation(AnnotationJSExport.class).value());
        }
        rst.put("NODE_TYPE", cnm);

        // Device
        Map<Integer, AbstractOperator> dos = deviceManager.getAllDeviceOperator();
        HashMap<Integer, String> deviceTypeList = new HashMap<>();
        for (Integer i : dos.keySet()) {
            // 输出设备类型
            AbstractOperator deviceOperator = dos.get(i);
            deviceTypeList.put(i, deviceOperator.getClass().getAnnotation(AnnotationJSExport.class).value());


            // 输出设备的信号和动作
            HashMap<Integer, String> deviceSig = new HashMap<>();
            HashMap<Integer, String> deviceAction = new HashMap<>();

            String classFullName = deviceOperator.getClass().getName();
            String className = classFullName.substring(classFullName.lastIndexOf('.') + 1);

            for (Field field : deviceOperator.getClass().getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    AnnotationJSExport annotationDescription = field.getAnnotation(AnnotationJSExport.class);
                    if (annotationDescription != null) {
                        //需要输出的常量
                        String fieldName = field.getName();
                        if (fieldName.indexOf("SIG_") == 0) {
                            // 信号常量
                            try {
                                deviceSig.put(field.getInt(null), annotationDescription.value());
                            } catch (IllegalAccessException e) {

                            }

                        }

                        if (fieldName.indexOf("ACT_") == 0) {
                            // 动作常量
                            try {
                                deviceAction.put(field.getInt(null), annotationDescription.value());
                            } catch (IllegalAccessException e) {

                            }
                        }
                    }
                }

                if (!deviceSig.isEmpty()) {
                    rst.put("DEVICE_SIG_" + i, deviceSig);
                }

                if (!deviceAction.isEmpty()) {
                    rst.put("DEVICE_ACT_" + i, deviceAction);
                }
            }

        }
        rst.put("DEVICE_TYPE", deviceTypeList);
        model.addAttribute("const", rst);

        return "const";
    }
}
