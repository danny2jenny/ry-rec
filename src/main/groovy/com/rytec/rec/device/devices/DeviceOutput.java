package com.rytec.rec.device.devices;


import com.rytec.rec.device.BaseDevice;
import com.rytec.rec.util.DeviceType;
import org.springframework.stereotype.Service;

/**
 * Created by danny on 16-11-29.
 * 输出
 * 三个端口：
 * control：开启、关闭
 * feedback：辅助节点，判断开启、关闭是否成功
 * remote：本地、远程
 */
@Service
@DeviceType(101)
public class DeviceOutput extends BaseDevice {

}
