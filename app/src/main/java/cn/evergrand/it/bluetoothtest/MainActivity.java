package cn.evergrand.it.bluetoothtest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.wenshenghui.bluetoothtest.R;

import java.util.UUID;

import cn.evergrand.it.bluetooth.BluetoothManager;
import cn.evergrand.it.bluetooth.bean.BlueToothWriteParams;
import cn.evergrand.it.bluetooth.connect.response.BleWriteResponse;
import cn.evergrand.it.bluetooth.utils.DataUtils;
import cn.evergrand.it.bluetooth.utils.ParseData;
import cn.evergrand.it.bluetooth.utils.UUIDUtils;
import cn.evergrand.it.bluetooth.utils.type.DataType;
import cn.evergrand.it.bluetoothtest.searchlist.BTDListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        //test();
        //测试数据打包接口
        //testPackingData();
        //测试数据解析接口
        //testParseData();
        //测试write接口
        //testWrite();
    }

    private void testWrite() {
        byte[] data = new byte[11];
        String mac = "00:0D:6F:2C:9F:4F";
        UUID uuid = UUIDUtils.makeUUID(1);
        UUID uuid1 = UUIDUtils.makeUUID(2);
        BlueToothWriteParams params = new BlueToothWriteParams(data, mac, uuid, uuid1,
                true, DataType.DOOR_HOME, 1,
                new BleWriteResponse() {
                    @Override
                    public void onResponse(int code, byte[] data, int requestId) {
                        Log.d("wsh_log", "BleWriteResponse code = " + code);
                    }
                });
        BluetoothManager.getInstance().write(params);
    }

    private void testParseData() {
        byte[] data = new byte[11];
        data[0] = 0x3A;
        data[1] = 11;
        data[2] = 0;
        data[3] = 1;
        data[4] = 0;
        data[5] = 0;
        data[6] = 2;
        data[7] = 0;
        data[8] = 0;
        data[9] = 0;
        data[10] = 72;
        ParseData parseData = DataUtils.parseData(true, data, null);
        byte[] data1 = parseData.mData;
        int requestId = parseData.mRequestId;
        if (data1 == null) {
            Log.d("wsh_log", "data1 == null");
            return;
        }
        Log.d("wsh_log", "data1.length = " + data1.length);
        Log.d("wsh_log", "data1 = " + data1[0]);
        Log.d("wsh_log", "requestId = " + requestId);
    }

    private void testPackingData() {
        byte[] data = new byte[2];
        data[0] = 2;
        data[1] = 3;
        byte[] bytes = DataUtils.packingData(data, true, DataType.DOOR_HOME, -129, null);
        Log.d("wsh_log", bytes.toString());
    }

    private void test() {
        byte[] data = new byte[1];
        data[0] = 1;
        UUID uuid = UUIDUtils.makeUUID(1);
        UUID uuid1 = UUIDUtils.makeUUID(2);
        BlueToothWriteParams params = new BlueToothWriteParams(data, "00:0D:6F:2C:9F:4F", uuid,
                uuid1,true,
                DataType.DOOR_HOME, 1, new BleWriteResponse() {
            @Override
            public void onResponse(int code, byte[] data, int requestId) {
                Log.d("wsh_log", "code");
            }
        });
        BluetoothManager.getInstance().write(params);

        byte[] data1 = new byte[11];
        data1[0] = 0x3A;
        data1[1] = 11;
        data1[2] = 0;
        data1[3] = 1;
        data1[4] = 0;
        data1[5] = 0;
        data1[6] = 2;
        data1[7] = 0;
        data1[8] = 0;
        data1[9] = 0;
        data1[10] = 71;
        ParseData parseData = DataUtils.parseData(true, data1, null);
        byte[] bytes = parseData.mData;
        int requestId = parseData.mRequestId;
        Log.d("wsh_log", "bytes = " + bytes + "; requestId = " + requestId);
    }


    private void initView() {
        findViewById(R.id.btn_start_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToBlueToothDevicesListActivity();
            }
        });
    }

    /**
     * 跳转到扫描页，并展示扫描结果
     */
    private void jumpToBlueToothDevicesListActivity() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            boolean hasPermission = checkLocationPermision();
            if (!hasPermission) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
        }
        Intent i = new Intent(MainActivity.this, BTDListActivity.class);
        startActivity(i);
    }

    public boolean checkLocationPermision() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode){
            // requestCode即所声明的权限获取码
            case 1:{
                int grantResult = grantResults[0];
                boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
                if (granted) {
                    Intent i = new Intent(MainActivity.this, BTDListActivity.class);
                    startActivity(i);
                }

            }break;
        }
    }
}
