package cn.evergrand.it.bluetooth.utils.type;

import android.support.annotation.IntDef;

/**
 * author: wenshenghui
 * created on: 2018/7/10 19:13
 * description: 数据类型
 */
public class DataType {

    /**
     * 小区大门
     */
    public static final int DOOR_CELL = 0;

    /**
     * 家庭大门
     */
    public static final int DOOR_HOME = 1;

    // 自定义一个注解   设备Type
    @IntDef({DOOR_CELL, DOOR_HOME})
    public @interface BtDeviceDataType {}
}
