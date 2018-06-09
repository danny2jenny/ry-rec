package com.rytec.rec.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
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
public class AppManager extends RecBase implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    ApplicationContext context;

    // 系统是否准备好了，是否需要初始化
    private volatile boolean appNeedInit = false;

    // 所有的管理接口
    // 按照启动顺序进行排序，升序
    List<ManageableInterface> serviceInterfacesStart = new ArrayList<ManageableInterface>();

    @PostConstruct
    private void init() {
        Map<String, ManageableInterface> interfaceMap = context.getBeansOfType(ManageableInterface.class);
        for (ManageableInterface item : interfaceMap.values()) {
            //Class<? extends Object> itemClass = item.getClass();
            //Order order = itemClass.getAnnotation(Order.class);
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
     * ****Channel              400                             // Channel 的实现
     * NodeManager              300
     * ****Node                 100             否
     * DeviceManager            300
     * ****Device               100             否
     * VideoService             300
     * FileManager              300                             // 60870 文件管理
     */

    public void systemReload() {
        // 停止服务，最后运行的最先停止
        debug("----------------停止-----------------");
        for (int i = 0; i < serviceInterfacesStart.size(); i++) {
            debug("----------停止：" + serviceInterfacesStart.get(serviceInterfacesStart.size() - 1 - i).getClass());
            serviceInterfacesStart.get(serviceInterfacesStart.size() - 1 - i).stop();
        }

        debug("----------------启动-----------------");
        // 开始服务
        for (int i = 0; i < serviceInterfacesStart.size(); i++) {
            debug("++++++++++启动：" + serviceInterfacesStart.get(i).getClass());
            serviceInterfacesStart.get(i).start();
        }

    }

    /**
     * 根容器初始化完成，设置appNeedInit
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //
        if (event.getApplicationContext().getParent() == null) {
            appNeedInit = true;
        }
    }

    /**
     * 定时检查是否需要重新初始化系统
     */
    @Scheduled(fixedDelay = 1000)
    void firstCheck() {
        if (appNeedInit) {
            appNeedInit = false;
            systemReload();
        }
    }
}
