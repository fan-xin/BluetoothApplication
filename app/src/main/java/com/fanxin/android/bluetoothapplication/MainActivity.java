package com.fanxin.android.bluetoothapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity-app";
    //蓝牙适配器
    private BluetoothAdapter bluetoothAdapter;
    private Toast toast;

    //开始扫描打按钮
    private Button startButton;
    //用来控制状态的变量，默认为false
    private boolean mIsScanStart = false;

    private BluetoothLeScanner leScanner;

    private ScanSettings scanSettings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        startButton = (Button)findViewById(R.id.id_btn_start_scan);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"button click");

                if (!mIsScanStart){
                    //如果还没开始扫描，则开始扫描
                    startButton.setText("Stop Scan");
                    mIsScanStart = true;
                    scan(true);
                }else {
                    startButton.setText("Start Scan");
                    mIsScanStart = false;
                    scan(false);
                }

            }
        });

        Toast.makeText(MainActivity.this, "hello",Toast.LENGTH_SHORT).show();

        toast = Toast.makeText(MainActivity.this, " ",Toast.LENGTH_SHORT);

        final BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        Log.d(TAG,"开始检查手机");

        if (bluetoothAdapter != null){
            showToast("手机支持蓝牙！");
            Log.d(TAG,"手机支持蓝牙");
        }else {
            finish();
        }


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            showToast("手机不支持蓝牙BLE功能");
            finish();
        }else {
            showToast("手机支持蓝牙BLE功能");
            Log.d(TAG,"手机支持蓝牙BLE功能");
        }
        Log.d(TAG,"检查手机结束");


        //从adapter获取scanner
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG,"获取scanner");
            leScanner = bluetoothAdapter.getBluetoothLeScanner();
            scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(1000).build();
        }







//        startButton = (Button)findViewById(R.id.id_btn_start_scan);
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG,"button click");
//
//                if (!mIsScanStart){
//                    //如果还没开始扫描，则开始扫描
//                    startButton.setText("Stop Scan");
//                    mIsScanStart = true;
//                    scan(true);
//                }else {
//                    startButton.setText("Start Scan");
//                    mIsScanStart = false;
//                    scan(false);
//                }
//
//            }
//        });


        scan(true);

    }

    @TargetApi(23)
    private void scan(boolean enable){
        Log.d(TAG,"scan");
        final ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                //打印出名字和mac地址
                //Log.d(TAG,"打印设备名称和MAC地址");
                Log.d(TAG,"name = " + device.getName() +", address = "+
                        device.getAddress());
            }
        };
        if (enable){
            Log.d(TAG,"start Scan");
            leScanner.startScan(scanCallback);
            Log.d(TAG,"start Scan over");
        }else {
            leScanner.stopScan(scanCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter!=null&&bluetoothAdapter.isEnabled()){
            //申请打开蓝牙功能
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
        }
    }

    private void showToast(String msg){
        toast.setText(msg);
        toast.show();
    }
    /**
     *动态申请权限
     *@author Fan Xin <fanxin.hit@gmail.com>
     *@time
     */
    private void requestPermissions() {
        if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)){
            //已经有权限，直接做操作
        }else {
            //没有权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
            }else {
                //申请权限
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
            }
        }
    }

    public void onclick(View view) {
        scan(true);
        Log.d(TAG,"onClick ");
        if (!mIsScanStart){
            //如果还没开始扫描，则开始扫描
            startButton.setText("Stop Scan");
            mIsScanStart = true;
            scan(true);
        }else {
            startButton.setText("Start Scan");
            mIsScanStart = false;
            scan(false);
        }

    }
}
