package cn.evergrand.it.bluetooth.utils;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Method;

/**
 * author: wenshenghui
 * created on: 2018/7/9 14:24
 * description:
 */
public class ClsUtils {

    @SuppressWarnings("unchecked")
    static public boolean setPin(Class btClass, BluetoothDevice btDevice, String str) {

        try {
            Method method = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
            method.invoke(btDevice, new Object[]{str.getBytes()});

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }

    //确认配对

    static public void setPairingConfirmation(Class<?> btClass,BluetoothDevice device,boolean isConfirm)throws Exception
    {
        Method setPairingConfirmation = btClass.getDeclaredMethod("setPairingConfirmation",boolean.class);
        setPairingConfirmation.invoke(device,isConfirm);
    }
}
