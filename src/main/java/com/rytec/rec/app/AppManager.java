package com.rytec.rec.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by danny on 17-2-12.
 * 整个应用的管理
 * 1、数据库初始化
 * 2、刷新配置
 */
@Service
public class AppManager {

    @Autowired
    ApplicationContext context;

    // 所有的管理接口
    Map<String, ManageableInterface> serviceInterfaces;

    @PostConstruct
    private void init() {
        serviceInterfaces = context.getBeansOfType(ManageableInterface.class);
    }

    /**
     * 重新启动服务器，重新加载配置
     * todo: 可能需要考虑启动和停止的顺序
     */

    public void systemReload() {
        for (ManageableInterface manageableInterface: serviceInterfaces.values()){
            manageableInterface.stope();
        }

    }

    /**
     * 停止服务
     */
    public void systemStop() {

    }

    /**
     * 启动服务器
     */
    public void systemStart() {

    }
}
