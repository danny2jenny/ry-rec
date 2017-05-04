package com.rytec.rec.app;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by danny on 17-2-12.
 * 整个应用的管理
 * 1、数据库初始化
 * 2、刷新配置
 */
@Service
@Order(500)
public class AppManager {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ApplicationContext context;

    // 所有的管理接口
    // 按照启动顺序进行排序，升序
    List<ManageableInterface> serviceInterfacesStart = new ArrayList<ManageableInterface>();

    @PostConstruct
    private void init() {
        Map<String, ManageableInterface> interfaceMap = context.getBeansOfType(ManageableInterface.class);
        for (ManageableInterface item : interfaceMap.values()) {
            Class<? extends Object> itemClass = item.getClass();
            Order order = itemClass.getAnnotation(Order.class);
            serviceInterfacesStart.add(item);
        }

        // 排序
        Collections.sort(serviceInterfacesStart, new Comparator<ManageableInterface>() {
            @Override
            public int compare(ManageableInterface o1, ManageableInterface o2) {
                return o1.getClass().getAnnotation(Order.class).value() - o2.getClass().getAnnotation(Order.class).value();
            }
        });
    }

    /**
     * 重新启动服务器，重新加载配置
     * <p>
     * 对象                   初始化级别       是否考虑Reload？
     * DbConfig                 200
     * CooperateManager         300
     * ChannelManager           300
     * ****Channel              400
     * NodeManager              300
     * ****Node                 100             否
     * DeviceManager            300
     * ****Device               100             否
     * VideoService             300
     */

    public void systemReload() {
        // 停止服务，最后运行的最先停止
        for (int i = 0; i < serviceInterfacesStart.size(); i++) {
            serviceInterfacesStart.get(serviceInterfacesStart.size()-1 - i).stop();
        }

        // 开始服务
        for (int i = 0; i < serviceInterfacesStart.size(); i++) {
            serviceInterfacesStart.get(i).start();
        }

    }
}
