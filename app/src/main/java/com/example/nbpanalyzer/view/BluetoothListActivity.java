package com.example.nbpanalyzer.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nbpanalyzer.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author SZLY(COPYRIGHT 2018 SZLY. All rights reserved.)
 * @abstract 主界面的函数设计
 * @version V1.0.0
 * @date  2019/01/01
 */

public class BluetoothListActivity extends AppCompatActivity {

    private static final boolean D = true;
    private static final String TAG = "BluetoothChat";
    public static String DEVICE_ADDRESS = "device_address";

    private Button scanButton;

    private BluetoothAdapter mBluetoothAdapter;
    /**
     * 已经配对蓝牙适配器
     */
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    /**
     * 扫描到的蓝牙适配器
     */
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    /**
     * @method onCreate方法
     * @param savedInstanceState 用户按到home键，退出了界面，使用
     * Bundle savedInstanceState就可以用户再次打开应用的时候恢复的原来的状态。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);

        scanButton = (Button) findViewById(R.id.btn_scan);
        mPairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        //已配对列表
        ListView pairedListView = (ListView) findViewById(R.id.lv_devices);
        //列表格式
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        //按下监听
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        ListView newDevicesListView = (ListView) findViewById(R.id.lv_new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        //当找到设备后注册广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        //Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);

        //获取本地蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //将每个已配对蓝牙显示出来
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            findViewById(R.id.tv_title_paired_devices).setVisibility(View.VISIBLE);

            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() +
                        "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
            }
        });
    }

    /**
     * @method 搜索蓝牙设备
     */
    private void doDiscovery() {
        if (D) {
            Log.d(TAG, "doDiscovery()");
        }
        setTitle(R.string.scanning);
        //使TextView可见
        findViewById(R.id.tv_title_new_devices).setVisibility(View.VISIBLE);

        //已经搜索完成，则停止搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mNewDevicesArrayAdapter.clear();
        //开始搜索设备
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * @method 注册广播
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device;
            //当找到新设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //获取打开的蓝牙设备
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //若为已配对，即不添加到new device
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                }
                //当搜索完毕
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                //已经搜索完成，则停止搜索
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:  //正在配对
                        Toast.makeText(getApplicationContext(),
                                "正在配对 ",
                                Toast.LENGTH_SHORT).show();
                        setTitle(R.string.pairing_device);
                        break;
                    case BluetoothDevice.BOND_BONDED:  //配对成功
                        setTitle(R.string.device_already_paired);
                        Toast.makeText(getApplicationContext(),
                                "完成配对 ",
                                Toast.LENGTH_SHORT).show();
                        mNewDevicesArrayAdapter.remove(device.getName() + "\n"
                                + device.getAddress());
                        mPairedDevicesArrayAdapter.add(device.getName() + "\n" +
                                device.getAddress());
                        break;
                    case BluetoothDevice.BOND_NONE:  //取消配对/未配对
                        Toast.makeText(getApplicationContext(),
                                "配对失败或取消 ",
                                Toast.LENGTH_SHORT).show();
                        setTitle(R.string.fail_to_pair);
                        break;
                    default:
                        break;
                }
            }
        }
    };
    /**
     * @method list按下后连接设备
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        //选项点击事件
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            //停止搜索设备
            mBluetoothAdapter.cancelDiscovery();
            //获取MAC地址
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //根据地址获取设备
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            //作为客户端连接设备
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                Intent intent = new Intent();
                intent.putExtra(DEVICE_ADDRESS, address);

                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                try {
                    Method createBond = BluetoothDevice.class.getMethod("createBond");
                    createBond.invoke(device);
                } catch (NoSuchMethodException | IllegalAccessException
                        | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    /**
     * @method 关闭BluetoothListAcitity触发
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

}
