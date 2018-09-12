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
import cn.evergrand.it.bluetooth.utils.UUIDUtils;
import cn.evergrand.it.bluetooth.utils.type.DataType;
import cn.evergrand.it.bluetoothtest.searchlist.BTDListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        test();
    }

    private void test() {
        byte[] data = new byte[1];
        data[0] = 1;
        UUID uuid = UUIDUtils.makeUUID(1);
        UUID uuid1 = UUIDUtils.makeUUID(2);
        BlueToothWriteParams params = new BlueToothWriteParams(data, "00:0D:6F:2C:9F:4F", uuid,
                uuid1,true,
                DataType.DOOR_HOME, new BleWriteResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                Log.d("wsh", "code");
            }
        });
        BluetoothManager.getInstance().write(params);

        byte[] data1 = new byte[11];
        data1[0] = 0x3A;
        data1[1] = 11;
        data1[2] = 0;
        data1[3] = 0;
        data1[4] = 0;
        data1[5] = 0;
        data1[6] = 2;
        data1[7] = 0;
        data1[8] = 0;
        data1[9] = 0;
        data1[10] = 71;
        byte[] bytes = DataUtils.parseData(true, data1, null);
        Log.d("wsh", "bytes = " + bytes);
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
