# BluetoothApplication

BLE(Bluetooth Low Energy)开发

profile 规范
不同的通信使用不同的profile

service 服务
一个profile中包含多个service

characteristic 特征值
一个service中包含多个characteristic

-----------------------------------------------------
BLE 低功耗蓝牙
蓝牙4.0

特性
超低打峰值，平均和待机功耗 一个纽扣电池，可以用2-3年
芯片低成本
不同厂商设备交互性（之前的蓝牙2.0，安卓和iphone的蓝牙无法传输文件）
无线覆盖范围增强
完全向下兼容
低延时

缺点是数据传输速率低，普遍用于穿戴设备，一次可以传输20字节
-----------------------------------------------------
应用场景 iBeacon技术

蓝牙防丢器
工作原理主要是通过距离变化来判断物品是否还控制在你的安全范围

SensorTag 开发套件
-----------------------------------------------------
使用蓝牙设备

申请使用蓝牙设备权限
查看收集是否支持蓝牙设备和蓝牙4.0功能
打开蓝牙功能
扫描附近打设备

uses-feature用来配置app在运行时所依赖的外部硬件或软件特征，uses-feature还提供了一个required属性配置，表示此项依赖的软硬件特征是否是必须的，当它设置为true表示此app运行时必须使用此项特征，如果没有则无法工作，false表示大部分功能可用，只是如何硬件不支持的话，只是对应的相关功能不可用。

BluetoothGattCallback是BLE设备连接以及通信过程中状态变化打主要返回调函数。
GATT
通用属性

蓝牙4.0通信
-----------------------------------------------------
BLE通信都是建立在GATT协议之上，是一个在蓝牙连接上的发送和接收很短数据段的通用规范。
数据段称为属性

GAP给设备定义角色，外围设备和中心设备
例如小米手环和手机

GATT 普通属性协议
Service和Characteristic
使用ATT协议
GAP协议------》 GATT连接

一个BLE外设同时只能被一个中心设备连接
一旦被连接，就会马上停止广播，这样对其他设备就不可见了。
当设备断开，又开始广播

GATT是CS结构
中心设备是客户端

GATT是蓝牙连接之上数据的通用规范，Peripheral和Central是GAP定义的两个角色
GAP用来控制设备的连接和广播

Profile包含若干Service，Service中包含若干Characteristic

通过特征值BluetoothGattCharacteristic的getValue()方法来返回字节数组数据。
setValue设置数据

通过Gatt来读写传感器
读取传感器
gatt.readCharacteristic()
写入传感器
configchar，setvalue()
gatt.writeCharacteristic()
