package com.fanxin.android.bluetoothapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity-app";

    //状态值
    private static final int BLE_DISCONNECTED = 0;
    private static final int BLE_CONNECTING = 1;
    private static final int BLE_CONNECTED = 2;

    //蓝牙适配器
    private BluetoothAdapter bluetoothAdapter;

    private Toast toast;

    //开始扫描打按钮
    private Button startButton;
    //用来控制状态的变量，默认为false
    private boolean mIsScanStart = false;

    private BluetoothLeScanner leScanner;

    private ScanSettings scanSettings;

    private TextView textView;
    //保存扫描到的MAC地址
    private String bleAddress = " ";

    private boolean IsConnected = false;

    private Button connectButton;

    //用于线程间通信的handler
    private Handler mMainUIHander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //接收到
            int newState = msg.what;
            if (newState == BluetoothProfile.STATE_CONNECTED){
                IsConnected = true;
                connectButton.setText("断开");
                showToast("连接成功！");
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                IsConnected = false;
                connectButton.setText("连接");
                showToast("设备已经断开");
            }





        }
    };

    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);

        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);

        }

        //newState表示当前的连接状态
        //连接上以后，会触发这个函数打执行

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            //发送新的state
            mMainUIHander.sendEmptyMessage(newState);

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        startButton = (Button)findViewById(R.id.id_btn_start_scan);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG,"222222222");

                Toast.makeText(MainActivity.this,"connect Button clicked",Toast.LENGTH_SHORT).show();

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


        connectButton = (Button)findViewById(R.id.id_btn_connect);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG,"111111111");

                Toast.makeText(MainActivity.this,"connect Button clicked",Toast.LENGTH_SHORT).show();

                if (!IsConnected){
                    connect();
                }else {
                    disconnect();
                }


            }
        });

        textView = (TextView)findViewById(R.id.id_tv_ble);

        requestPermissions();

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


        //scan(true);

    }

    private boolean connect(){
        //传入MAC地址
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bleAddress);
        bluetoothGatt = device.connectGatt(MainActivity.this,false,callback);

        if (bluetoothGatt != null){
            return true;
        }else {
            return false;
        }


    }

    private void disconnect(){
        if (bluetoothGatt != null){
            bluetoothGatt.disconnect();
        }
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
                textView.setText(device.getName()+" - "+device.getAddress());
                //记录扫描到打地址
                bleAddress = device.getAddress();
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

//    public void onclick(View view) {
//        scan(true);
//        Log.d(TAG,"onClick ");
//        if (!mIsScanStart){
//            //如果还没开始扫描，则开始扫描
//            startButton.setText("Stop Scan");
//            mIsScanStart = true;
//            scan(true);
//        }else {
//            startButton.setText("Start Scan");
//            mIsScanStart = false;
//            scan(false);
//        }
//
//    }
}
