package com.example.nbpanalyzer.communication;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.nbpanalyzer.Bean.ProParaBoardData;
import com.example.nbpanalyzer.MainActivity;
import com.example.nbpanalyzer.utils.PackUnPack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author SZLY(COPYRIGHT 2018 SZLY. All rights reserved.)
 * @abstract 蓝牙服务器
 * @version V1.0.0
 * @date  2019/01/01
 */
public class BluetoothService extends Service {
    private static final int STATE_NONE = 0;
    private static final int STATE_LISTEN = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;

    private static final boolean D = true;
    private static final String TAG = "BluetoothService";
    private static final String NAME = "BluetoothChat";
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private int mState;
    private final Handler mHandler;
    private final BluetoothAdapter mBluetoothAdapter;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private PackUnPack packUnpack;
    public ProParaBoardData proParaBoardData;
    private boolean mUnpackStatus;
    /**
     * @method 构造函数
     * @param context UI活动背景
     * @param handler 界面间传递数据
     */
    public BluetoothService(Context context, Handler handler) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        packUnpack = new PackUnPack();
        proParaBoardData = new ProParaBoardData();
        mUnpackStatus = false;
    }
    /**
     * @method 设置前蓝牙状态值
     * @param state 状态值
     */
    private synchronized void setState(int state) {
        if (D) {
            //打印目前状态
            Log.e(TAG, "setState() " + mState + " -> " + state);
        }
        mState = state;
    }
    /**
     * @method 获取当前蓝牙连接状态
     * @return 目前连接状态
     */
    public synchronized int getState() {
        return mState;
    }
    /**
     * @method 连接失败时设置状态为监听，并发送消息
     */
    private void connectionFailed() {
        Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST_FAIL);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST,
                "客户端连接失败，转为服务端监听连接");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_LISTEN);
    }
    /**
     * @method 掉连接时设置状态为监听，并发送消息
     */
    private void connectionLost() {

        Message msg = mHandler.obtainMessage
                (MainActivity.MESSAGE_TOAST_LOST);
        //在界面发送一个通知的结果
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "蓝牙连接中断");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        this.stop();
    }
    /**
     * @method 作为客户端连接蓝牙设备
     * @param device 待连接的蓝牙设备
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) {
            Log.e(TAG, "connect to: " + device);
        }
        //Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        //Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        //Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();

        setState(STATE_CONNECTING);

    }
    /**
     * @method 开始服务端监听线程
     */
    public synchronized void start() {

        if (D) {
            Log.e(TAG, "start");
        }

        //Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        //Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        //Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }

        setState(STATE_LISTEN);

    }
    /**
     * @method 通过已连接设备打开数据流
     * @param socket  已连接的socket通道
     * @param device  已连接设备
     */
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

        if (D) {
            Log.e(TAG, "connected");
        }

        //Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        //Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        //Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        //Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        //若连接蓝牙成功，传送蓝牙名字到主界面
        Message msg = mHandler.obtainMessage
                (MainActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);

    }

    /**
     * @method 发送数据
     * @param out 待发送的字节
     * @see ConnectedThread # write(byte[])
     */
    public void write(byte[] out) {
        //Create temporary object
        ConnectedThread connetedThread;
        //Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            connetedThread = mConnectedThread;
        }
        connetedThread.write(out);
    }
    /**
     * @method 停止连接线程和数据流线程
     */
    public synchronized void stop() {
        if (D) {
            Log.e(TAG, "stop");
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        setState(STATE_NONE);
    }
    /**
     * @method 设置状态
     * @return
     */
    public void setStatus(boolean status) {
        mUnpackStatus = status;
    }
    /**
     * @method 作为客户端连接
     * 调用connect()建立连接，当调用这个方法的时候，系统会在远程设备上完成一个
     * SDP查找来匹配UUID。
     * 如果查找成功并且远程设备接受连接，就共享RFCOMM信道，connect()会返回，
     * 这个方法也是一个阻塞的调用。
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        /**
         * 连接线程构造函数
         */
        @TargetApi(Build.VERSION_CODES.ECLAIR)
        private ConnectThread(BluetoothDevice device) {
            mDevice = device;
            BluetoothSocket tmp = null;

            try {
                //通过UUID获取一个BluetoothSocket对象
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mSocket = tmp;
        }

        /**
         * @method 连接线程方法体
         */
        @Override
        public void run() {
            setName("ConnectThread");
            //要确保在调用connect()时没有同时做设备搜索，
            //如果在搜索设备，该连接尝试会显著变慢，容易导致连接失败。
            mBluetoothAdapter.cancelDiscovery();

            try {
                //阻塞的调用
                mSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                //Close the socket
                try {
                    mSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                //客户端连接失败， 改用服务端连接
                BluetoothService.this.start();
                return;
            }
            synchronized (BluetoothService.this) {
                //关闭连接线程
                mConnectThread = null;
            }
            //开启数据流线程
            connected(mSocket, mDevice);

        }

        /**
         * 连接线程cancel方法
         */
        private void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * @method 作为服务端连接，服务器Socket的作用是侦听进来的连接，
     * 且在一个连接被接受时返回一个BluetoothSocket对象
     * 获取BluetoothSocket对象就应close，除非需连接更多
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mServerSocket;

        /**
         * @method 监听线程构造函数
         */
        @TargetApi(Build.VERSION_CODES.ECLAIR)
        private AcceptThread() {
            BluetoothServerSocket serverSocket = null;

            //创建一个监听服务端口，使用accept方法阻塞，当监测到
            //连接时，返回一个socket对象管理这个连接，可获取输入输出流
            try {
                //打开服务器端的RFCOMM信道获得BluetoothSocket
                //NAME为服务器Socket标识名，名字可以任意（可谓应用名）
                //当客户端试图连接本设备时，它将携带一个UUID用来唯一标识
                //它要连接的服务，UUID必须匹配，连接才会被接受
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord
                        (NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mServerSocket = serverSocket;
        }

        /**
         * @method 监听线程run方法体
         */
        @TargetApi(Build.VERSION_CODES.ECLAIR)
        @Override
        public void run() {
            //设置目前运行线程为AcceptThread
            setName("AcceptThread");
            BluetoothSocket socket;

            while (mState != STATE_CONNECTED) {
                try {
                    //该方法会阻塞, 直到监听到一个连接或异常才返回
                    socket = mServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                //已监听到一个连接，就打开数据流
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    //关闭服务端口，因为已有连接，已开启数据流，
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                            default:
                                break;
                        }

                    }

                }
            }
        }

        /**
         * @method 监听线程cancel方法
         */
        @TargetApi(Build.VERSION_CODES.ECLAIR)
        private void cancel() {
            if (D) {
                Log.e(TAG, "cancel " + this);
            }
            try {
                mServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    /**
     * @method 打开数据流线程
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;

        /**
         * @method 数据流线程构造函数
         */
        private ConnectedThread(BluetoothSocket socket) {
            Log.e(TAG, "create ConnectedThread");
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }

        /**
         * @method 数据流线程方法体
         */
        @Override
        public void run() {
            Log.e(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            int data;

            while (true) {
                try {
                    //读字节流
                    bytes = mInStream.read(buffer);
                    if(mUnpackStatus) {
                        for (int i = 0; i < bytes; i++) {
                            data = buffer[i];
                            data = data >= 0 ? data : (data + 256);
                            //对数据进行解包
                            if (packUnpack.unPackData(data)) {
                                //解包成功，进行数据处理
                                proParaBoardData.proData(packUnpack.getUnPackResult());
                            }
                        }
                    }

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * @method 写字节流
         * @param buffer 待写的字节流
         */
        private void write(byte[] buffer) {
            try {
                mOutStream.write(buffer);

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
        /**
         * @method 数据流线程cancel方法
         */
        @TargetApi(Build.VERSION_CODES.ECLAIR)
        private void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    @Override
   public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
   }
}
