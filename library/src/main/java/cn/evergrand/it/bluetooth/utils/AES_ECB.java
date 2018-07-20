package cn.evergrand.it.bluetooth.utils;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2018-4-18.
 */

public class AES_ECB {
    private static final String TAG = "ble";   //AESH 加密

    private static final String AES = "AES";   //AESH 加密
    private static final String Padding = "AES/ECB/NoPadding"/*"AES/ECB/PKCS5Padding"*/;
    private static final byte[] N_16 = {0x49, 0x36, 0x55, (byte) 0x87, (byte) 0xc1, (byte) 0x98, 0x2a, 0x17,
            (byte) 0x84, 0x55, (byte) 0x91, 0x35, 0x61, (byte) 0x90, (byte) 0x89, 0x48};
    private byte[] N_4;
    private byte[] openDoor;
    private static byte[] TEST = {0x01, 0x02, 0x03, 0x04};

    public AES_ECB() {
        this.openDoor = openDoorData2();
    }

    /*
     * 设置蓝牙广播数据中的有效四位密码
     * */
    public void setN_4(byte[] n_4) {
        N_4 = n_4;
    }

    public byte[] getN_4() {
        return N_4;
    }

    public byte[] getOpenDoor() {
        return openDoor;
    }

    /*
     * 设置开门初始化数据
     * */
    public void setOpenDoor(byte[] card) {
       /* System.arraycopy(card, 0,
                openDoor, 3, card.length);*/
        //LogUtils.log(TAG, openDoor.length + "  xxxxx " + openDoor[3] + " x x " + openDoor[4] + " cc " + openDoor[5]);
        byte[] aesbyte = aesEcb(card);
        System.arraycopy(aesbyte, 0,
                openDoor, 3, aesbyte.length);
        //LogUtils.log(TAG, openDoor.length + "  xxxxx " + openDoor[0] + " x x " + openDoor[1] + " cc " + openDoor[5]);
        openDoor[openDoor.length - 1] = (byte) sumOpenByte2();
        //LogUtils.log(TAG, openDoor[openDoor.length - 1] + " xxxxxxxxxxxxxxxxxx ");

        //LogUtils.log(TAG, " data open:  " + byte2HexString(openDoor));
    }

    private byte sumOpenByte() {
        byte byteSum = 0;
        for (int i = 0; i < openDoor.length; i++) {
            //LogUtils.log(TAG, byteSum + " openDoor: " + openDoor[i]);
            byteSum = (byte) ((openDoor[i] + byteSum) & 0xFF);
            //LogUtils.log(TAG, byteSum + " byteSum ");
        }
        return byteSum;
    }

    private int sumOpenByte2() {
        int byteSum = 0;
        for (int i = 0; i < openDoor.length - 1; i++) {
            //LogUtils.log(TAG, byteSum + " openDoor: " + openDoor[i]);
            byteSum = (((openDoor[i] & 0xFF) + byteSum));
            //LogUtils.log(TAG, byteSum + " byteSum " + (openDoor[i] & 0xFF));
        }
        int sum = byteSum % 0xFF;
        //LogUtils.log(TAG, byteSum + " xxxxxxxxxxx byteSum " + sum);
        return sum;
    }


    /*
     * 数据加密
     * */
    private byte[] aesEcb(byte[] card) {
        //获取需要加密的数据
        // byte[] data = subBytes(openDoor, 3, 22);
        byte[] data = new byte[32];
        if (card.length % 16 != 0) {
            System.arraycopy(card, 0,
                    data, 0, card.length);
        }
        //LogUtils.log(TAG, data[0] + " nnn " + data[1] + " ble data " + data[data.length - 1]);
        //LogUtils.log(TAG, card[0] + " nnn " + card[1] + " ble data " + card[card.length - 1]);
        byte[] source = encrypt(data, N_16);
        //LogUtils.log(TAG, source.length + " ble source " + source[card.length - 1]);
        return source;
    }


    /*
     * 数据加密
     * */
    private byte[] aesEcbComplex(byte[] bleData) {
        //LogUtils.log(TAG, bleData[0] + " ble N_4 " + bleData[1]);
        //获取密钥
        byte[] key = calculateKey(bleData);
        //获取需要加密的数据
        byte[] data = subBytes(openDoor, 8, 58);
        //LogUtils.log(TAG, data[0] + " ble data " + data[data.length - 1]);
        byte[] source = encrypt(data, key);
        //LogUtils.log(TAG, source.length + " ble source " + source[data.length - 1]);
        return source;
    }

    public byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    private byte[] openDoorData() {
        byte[] openDoor = {0x3a,//帧头 1byte
                0x00, 0x00,//保留2byte
                0x01,//数据类型，开门0x01，1byte
                0x14,//开号有效长度，1byte
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, //卡号内容，20 byte
                0x00,//累加和 前面所有字节相加
        };
        return openDoor;
    }

    private byte[] openDoorData2() {
        byte[] openDoor = {0x3a,//帧头 1byte
                0x24, 0x00,//保留2byte
                0x01,//数据类型，开门0x01，1byte
                0x14,//开号有效长度，1byte
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                //卡号内容，30 byte
                0x00,//累加和 前面所有字节相加
        };
        return openDoor;
    }

    private byte[] openDoorDataComplex() {
        byte[] openDoor = {0x00, (byte) 0xFF,
                0x00, 0x00, 0x00, 0x00,//4 byte 预留
                0x40, 0x00,//data长度 2 byte
                0x06, 0x02,//data-1有效长度 2 byte 有效数据8

                0x00, 0x00,//2 byte 预留
                0x48, 0x45, 0x4e, 0x47, 0x44, 0x41,/*HENGDA 20 byte*/ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x14, 0x00,//data-2有效长度 2 byte 有效数据20
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //32 byte 卡号，实际20

                0x00, 0x00 //累加和 2 byte

        };
        return openDoor;
    }

    public static String byte2HexString(byte[] bytes) {
        String hex = "";
        if (bytes != null) {
            for (Byte b : bytes) {
                hex += String.format("%02X", b.intValue() & 0xFF);
            }
        }
        //LogUtils.log(TAG, " byte2HexString: " + hex);
        return hex;
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
        }
        return ret;
    }

    /*
     * 数据换算
     * */
    public byte[] calculateKey(byte[] bleData) {
        byte[] newN_16 = new byte[16];
        for (int i = 0; i < N_16.length; i++) {
            if (i < 4) {
                //& 与
                newN_16[i] = andHex(N_16[i], bleData[0]);

            } else if (i >= 4 && i < 8) {
                //或
                newN_16[i] = orHex(N_16[i], bleData[1]);

            } else if (i >= 8 && i < 12) {
                //异或
                newN_16[i] = xorHex(N_16[i], bleData[2]);

            } else if (i >= 12 && i < 16) {
                //加
                newN_16[i] = addHex(N_16[i], bleData[3]);
            }
        }
        Log.i("sz", "newN_16: " + newN_16[8]);
        return newN_16;
    }

    /*
     * 数据换算
     * */
    public byte[] calculateKey1() {
        byte[] newN_16 = new byte[16];
        for (int i = 0; i < N_16.length; i++) {
            if (i < 4) {
                //& 与
                newN_16[i] = andHex(N_16[i], TEST[0]);

            } else if (i >= 4 && i < 8) {
                //或
                newN_16[i] = orHex(N_16[i], TEST[1]);

            } else if (i >= 8 && i < 12) {
                //异或
                newN_16[i] = xorHex(N_16[i], TEST[2]);

            } else if (i >= 12 && i < 16) {
                //加
                newN_16[i] = addHex(N_16[i], TEST[3]);
            }
        }
        Log.i("sz", "newN_16: " + newN_16[8]);
        return newN_16;
    }

    /*
     * 十六进制加法
     * */
    private byte addHex(byte source, byte key) {
        byte b1 = (byte) source;
        byte b2 = (byte) key;
        //byte byteHex = Byte.parseByte(Integer.toHexString(b2 + b1), 16);
        byte byteHex = (byte) ((b2 + b1) & 0xFF);
        Log.i("sz", byteHex + " newSouce ");
        return byteHex;
    }

    /*
     *与运算
     * */
    private byte andHex(byte source, byte key) {
        byte newSouce = (byte) (source & key);
        //LogUtils.log("sz", newSouce + " andHex newSouce ");
        return newSouce;
    }

    /*
     *或运算
     * */
    private byte orHex(byte source, byte key) {
        byte newSouce = (byte) (source | key);
        //LogUtils.log("sz", newSouce + " orHex newSouce " + source);
        return newSouce;
    }

    /*
     *异或
     * */
    private static byte xorHex(byte source, byte key) {
        byte newSouce = (byte) (source ^ key);
        //LogUtils.log("sz", newSouce + " orHex newSouce ");
        return newSouce;
    }

    /**
     * 数据加密
     *
     * @param data
     * @return
     */
    public byte[] encrypt(byte[] data, byte[] key) {
        byte[] original = null;
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, AES);
            Cipher cipher = Cipher.getInstance(Padding);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            original = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return original;
    }

    /**
     * 数据解密
     *
     * @param encData
     * @return
     */
    public String decrypt(String encData, String key) {
        byte[] decodeBase64 = Base64.decode(encData, 0);
        byte[] original = null;
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(Padding);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            original = cipher.doFinal(decodeBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(original).trim();
    }

   /* public static void main(String[] args) throws Exception {
        //        String str = "20171017095514800000000000000000";
        //        String key = "f5663bc2165b9b50";
        //        byte[] encrypt_data = encrypt(str.getBytes(), key.getBytes());
        //        String s = decrypt(encrypt_data, key);
        //        System.out.println("加密前： : "+str);
        //        System.out.println("密文： : "+encrypt_data);
        //        System.out.println("解密后：   "+s);
    }*/
}
