package cn.evergrand.it.bluetooth.utils;

import cn.evergrand.it.bluetooth.encry.IBlueToothDecrypt;
import cn.evergrand.it.bluetooth.encry.IBlueToothEncryAndDecry;
import cn.evergrand.it.bluetooth.utils.type.DataType;

/**
 * author: wenshenghui
 * created on: 2018/7/10 11:47
 * description: 数据封装和解析工具
 */
public class DataUtils {

    /**
     * 头部信息
     */
    private static final byte sHead = 0x3a;

    /**
     * 根据策略类型解析数据
     * @param needParseAndPacking
     * @param value
     * @return
     */
    public static byte[] parseData(boolean needParseAndPacking, byte[] value, IBlueToothDecrypt decrypt) {
        if (value == null) {
            return value;
        }
        if (!needParseAndPacking) { //不做处理
            if (decrypt != null) {
                return decrypt.decrypt(value);
            }
            return value;
        }else if (decrypt == null) { //只做解析处理
            return DataUtils.realParseData(value);
        }else if (decrypt != null) {//先解析，后解密
            return DataUtils.parseAndDecry(value, decrypt);
        }

        return value;
    }

    /**
     * 根据策略类型封装数据
     * @param needParseAndPacking
     * @param type
     * @param value
     * @return
     */
    public static byte[] packingData(byte[] value, boolean needParseAndPacking, @DataType.BtDeviceDataType int type, IBlueToothEncryAndDecry encryAndDecry) {
        if (value == null) {
            return value;
        }
        if (!needParseAndPacking) { //不做处理
            if (encryAndDecry != null) {
                return encryAndDecry.encrypt(value);
            }
            return value;

        }else if (encryAndDecry == null) { //封装处理
            return DataUtils.realPackingData(value,type);
        } else if (encryAndDecry != null) {//先加密再封装
            return DataUtils.realPackingData(encryAndDecry.encrypt(value),type);
        }

        return value;
    }

    /**
     * 包装已加密数据，传给蓝牙模块
     * @param data 已加密的数据
     * @return
     */
    private static byte[] realPackingData(byte[] data, @DataType.BtDeviceDataType int type) {
        if (data == null) {
            return null;
        }
        byte[] newData = addHeadPart(data, type);
        if (newData == null || newData.length == 0) {
            return null;
        }
        newData[newData.length - 1] = (byte) sumData(newData, newData.length -1);
        return newData;
    }

    /**
     * 解析然后解密
     * @return
     */
    private static byte[] parseAndDecry(byte[] data, IBlueToothDecrypt decrypt) {
        if (data == null || decrypt == null) {
            return null;
        }

        byte[] parseData = realParseData(data);
        return decrypt.decrypt(parseData);
    }

    /**
     * 解析数据，检查并获取上层所需的数据
     * @param data
     * @return 如果不合法，返回null
     */
    private static byte[] realParseData(byte[] data) {
        if (data == null ) {
            return null;
        }

        boolean legal = checkLegal(data);
        if (!legal) {
            return null;
        }
        byte[] newData = getParseData(data);

        return newData;
    }

    /**
     *
     * @param data 原始数据
     * @return 解析原始数据从第六位开始截断
     */
    private static byte[] getParseData(byte[] data) {
        if (data == null || data.length < 12) {
            return null;
        }
        byte[] newData = ByteUtils.subBytes(data, 6, data.length - 11);
        return newData;
    }

    /**
     *
     * @param data 根据协定的数据格式，返回的数据长度必须 > 11 否则认为数据无效
     * @return
     */
    private static boolean checkLegal(byte[] data) {
        if (data == null || data.length < 12) {
            return false;
        }

        byte head = data[0];
        if (head != sHead) {//头部验证失败，数据不合法解析失败
            return false;
        }

        int lenForCheck = data[1] & 0xFF;
        if (data.length != lenForCheck) { //数据长度验证失败
            return false;
        }
        byte sumData = (byte) sumData(data, data.length - 2);
        byte sumPosData = data[data.length - 2];
        if (sumData != sumPosData) { //叠加和验证失败，数据不合法解析失败
            return false;
        }
        return true;
    }

    /**
     * 添加头部信息： 帧头，数据长度，保留
     * @param oldData
     * @type 类型
     * @return
     */
    private static byte[] addHeadPart(byte[] oldData, @DataType.BtDeviceDataType int type) {
        if (oldData == null) {
            return null;
        }
        int oldLength = oldData.length;
        byte[] newData = new byte[oldLength + 4];
        newData[0] = sHead;
        newData[1] = (byte) (newData.length);
        newData[2] = (byte) type;
        for (int i = 3; i < newData.length; i++) {
            newData[i] =  oldData[i - 1];
        }
        return newData;
    }

    /**
     * 计算data   0 到 length位的叠加和 % 0xff
     * @param data
     * @return
     */
    private static int sumData(byte[] data, int length) {
        if (data == null || data.length == 0) {
            return 0;
        }

        if (data.length < length) {
            return 0;
        }
        int byteSum = 0;
        for (int i = 0; i < length; i++) {
            byteSum = (data[i] & 0xFF) + byteSum;
        }
        int sum = byteSum % 0xFF;
        return sum;
    }
}
