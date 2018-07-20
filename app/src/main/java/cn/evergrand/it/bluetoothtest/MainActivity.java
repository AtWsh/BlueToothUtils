package cn.evergrand.it.bluetoothtest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.wenshenghui.bluetoothtest.R;

import cn.evergrand.it.bluetoothtest.searchlist.BTDListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
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
