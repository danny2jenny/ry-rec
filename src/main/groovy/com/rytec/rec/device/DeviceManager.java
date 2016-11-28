package com.rytec.rec.device;

/**
 * Created by danny on 16-11-14.
 * 通过这个接口和前端设备进行通信
 * 这里会根据前端设备的类型去调用相应的Device驱动
 * <p>
 * 这是所有与设备操作的入口
 */
public class DeviceManager {

    //前端设备数据反馈
    public void onData(int no, int type, float data) {

    }

    //前端设备状态反馈
    public void onState(int no, int type, int state) {

    }

    public void initChannel() {

    }

    public void initDevice() {

    }

}
