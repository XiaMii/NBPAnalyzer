package com.example.nbpanalyzer;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;

import com.example.nbpanalyzer.Bean.Nbp_data;
import com.example.nbpanalyzer.Bean.ProParaBoardData;
import com.example.nbpanalyzer.communication.BluetoothService;
import com.example.nbpanalyzer.utils.DbManger;
import com.example.nbpanalyzer.utils.PackUnPack;
import com.example.nbpanalyzer.model.HomeViewModel;
import com.example.nbpanalyzer.view.BluetoothListActivity;
import com.example.nbpanalyzer.view.loginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final int MESSAGE_DEVICE_NAME = 1;
    public static final int MESSAGE_TOAST_FAIL = 2;
    public static final int MESSAGE_TOAST_LOST = 3;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_BT_PERMISSION = 3;
    private static final int REQUEST_EXTERNAL_STORAGE = 4;
    private static final int REQUEST_LOGIN = 5;
    private static final String TAG = "MainActivity";
    private static final boolean D = true;
    private boolean BluetoothEnable = false;
    private boolean BluetoothConnect = false;
    private boolean mNbpStartMea = false;
    private boolean isMessure = false;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mChatService;
    private PackUnPack mPackUnPack;
    private HomeViewModel homeViewModel;
    private static ProParaBoardData mProParaBoardData;

    private AppBarConfiguration mAppBarConfiguration;

    private ImageButton userButton;
    private TextView userText;

    private FloatingActionButton bluetoothfab;
    private FloatingActionButton messurefab;

    /**
     * 历史记录操作的工具类
     */
    private DbManger dbManger;
    private List<Nbp_data> NbpDatas;
    /**
     * 血压开始测量和停止测量数据包
     */
    private byte[] mStartMeaBuffer;
    private byte[] mStopMeaBuffer;

    /**
     * 建立线程池，核心任务1个
     */
    private ScheduledExecutorService mExecutorService = new ScheduledThreadPoolExecutor(1);

    private messureThread MessureThread = new messureThread();
    private Context sContext;


    private BlueToothHandler mHandler = new BlueToothHandler();
    //新建一个处理蓝牙消息的Handler
    /**
     * @method 定义一个内部静态类，继承Handler
     */
    private class BlueToothHandler extends Handler {
        @Override//让编译器帮助自己检查是否正确的复写了父类中已有的方法
        public void handleMessage(Message msg){//处理消息函数
            switch (msg.what) {
                //连接成功，显示连接到的设备名字
                case MESSAGE_DEVICE_NAME:
                    String mConnectedDeviceName
                            = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(sContext,
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    ColorStateList colorStateList1 = ContextCompat.getColorStateList(getApplicationContext(),
                            R.color.colorPrimary);
                    bluetoothfab.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
                    bluetoothfab.setBackgroundTintList(colorStateList1);
                    BluetoothConnect = true;
                    break;
                case MESSAGE_TOAST_FAIL:
                    //客户端连接失败
                case MESSAGE_TOAST_LOST:
                    //连接失败或断掉信息
//                    Toast.makeText(MainActivity.sContext,
//                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
//                            .show();
                    ColorStateList colorStateList2 = ContextCompat.getColorStateList(getApplicationContext(),
                            R.color.colorLightGray);
                    bluetoothfab.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
                    bluetoothfab.setBackgroundTintList(colorStateList2);
                    BluetoothConnect = false;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sContext = getApplicationContext();
        dbManger = DbManger.getInstance(this);

        //获取本地蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
//            Toast.makeText(MainActivity.sContext,R.string.bt_not_enabled,
//                    Toast.LENGTH_SHORT).show();
            BluetoothEnable = false;
        }
        else{
            BluetoothEnable = true;
            mPackUnPack = new PackUnPack();
            mProParaBoardData = new ProParaBoardData();
            //获得开始测量血压和结束测量血压命令包
            initNbpBuffer();
            //500ms任务：获取血压信息
            mExecutorService.scheduleAtFixedRate(MessureThread, 0, 500, TimeUnit.MILLISECONDS);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bluetoothfab = findViewById(R.id.bluetoothButton);
        messurefab = findViewById(R.id.messureButton);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        //获取ViewModel,让ViewModel与此activity绑定
        messurefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BluetoothEnable){
                    if(BluetoothConnect){
                        //标志位为开始测量
                        if(!mNbpStartMea) {
                            //发送血压测量命令

                            mChatService.write(mStartMeaBuffer);
                            mChatService.setStatus(true);
                            mNbpStartMea = true;
                            ColorStateList colorStateList2 = ContextCompat.getColorStateList(getApplicationContext(),
                                    R.color.colorPrimary);
                            messurefab.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
                            messurefab.setBackgroundTintList(colorStateList2);
                            Snackbar.make(view, "血压计已启动测量", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            isMessure = true;
                            //MessureThread.interrupt();

                        } else {//停止测量
                            mNbpStartMea = false;
                            //发送血压结束测量命令
                            mChatService.write(mStopMeaBuffer);
                            //中断线程
                            //mExecutorService.shutdownNow();
                            isMessure = false;

                            ColorStateList colorStateList1 = ContextCompat.getColorStateList(getApplicationContext(),
                                    R.color.colorLightGray);
                            messurefab.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
                            messurefab.setBackgroundTintList(colorStateList1);
                            Snackbar.make(view, "血压计已停止测量", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }


                    }
                    else{
                        Snackbar.make(view, R.string.bt_not_connect, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
                else{
                    Snackbar.make(view, R.string.bt_not_enabled, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        bluetoothfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BluetoothEnable){
                    Intent serverIntent = new Intent(MainActivity.this, BluetoothListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                }
                else {
                    //用户没有蓝牙功能，提示
                    //Toast.makeText(MainActivity.sContext, R.string.bt_not_enabled,Toast.LENGTH_SHORT).show();
                }

            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //获取头部布局
        View navHeaderView = navigationView.getHeaderView(0);
        //设置监听事件
        userButton =navHeaderView.findViewById(R.id.imageButton);
        userText = navHeaderView.findViewById(R.id.tv_user);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serverIntent = new Intent(MainActivity.this, loginActivity.class);
                startActivityForResult(serverIntent, REQUEST_LOGIN);
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_record, R.id.nav_user,
                R.id.nav_analyse, R.id.nav_help, R.id.nav_setting)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



    }


    /**
     * @method Activity执行完onCreate后执行onStart
     */
    @Override
    public void onStart() {
        super.onStart();
        if (D) {
            Log.d(TAG, "On Start");
        }
        if(BluetoothEnable) {
            //监测蓝牙设备是否打开
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                //已打开，就启动蓝牙服务
            } else {
                if (mChatService == null) {
                    Log.d(TAG, "mChatService is null");
                    mChatService = new BluetoothService(this, mHandler);
                }
            }

            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_BT_PERMISSION);
            }

    }
    }

    /**
     * @method 打开的蓝牙list关闭后
     * @param requestCode 标识请求的来源
     * @param resultCode 标识返回的数据来自的activity
     * @param data BlueToothListActivity返回的数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //连接设备
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = Objects.requireNonNull(data.getExtras()).getString(
                            BluetoothListActivity.DEVICE_ADDRESS);
                    //获取此蓝牙
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    //连接待连接蓝牙设备
                    mChatService.connect(device);
                }
                break;
            //打开蓝牙
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    mChatService = new BluetoothService(this, mHandler);
                } else {
                    if (D) {
                        Log.d(TAG, "BT not enabled");
                    }
                    //用户不允许打开蓝牙，提示
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            //登录
            case REQUEST_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    String user = Objects.requireNonNull(data.getExtras()).getString(
                            loginActivity.USER_NAME);
                    userText.setText(user);
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_BT_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                } else
                {
                    Toast.makeText(MainActivity.this, "为了您的血压计能正常使用请允许权限", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                } else
                {
                    Toast.makeText(MainActivity.this, "为了保存您的血压数据请允许权限", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * @method 打包血压数据包
     */
    private void initNbpBuffer(){
        int[] testBuffer = new int[10] ;
        testBuffer[0] = 0x14;
        testBuffer[1] = 0x80;

        try {
            mPackUnPack.packData(testBuffer);
            mStartMeaBuffer = new byte[testBuffer.length];

            for (int i = 0; i < testBuffer.length; i++) {
                mStartMeaBuffer[i] = (byte)testBuffer[i];
            }

            testBuffer = new int[10];
            testBuffer[0] = 0x14;
            testBuffer[1] = 0x81;

            mPackUnPack.packData(testBuffer);

            mStopMeaBuffer = new byte[testBuffer.length];

            for (int i = 0; i < testBuffer.length; i++) {
                mStopMeaBuffer[i] = (byte)testBuffer[i];
            }
        } catch (Exception e) {
            Log.e(TAG, "packSuccess: ", e);
        }
    }
    class messureThread extends Thread{
        @Override
        public void run() {//更新数据
            Log.e(TAG, "mExecutorService: 数据有更新");
            if(isMessure) {
                homeViewModel.getNbpData().setSysPressure(mChatService.proParaBoardData.getSysPressure());
                homeViewModel.getNbpData().setNbpCuff(mChatService.proParaBoardData.getNbpCufPre());
                homeViewModel.getNbpData().setDisPressure(mChatService.proParaBoardData.getDisPressure());
                homeViewModel.getNbpData().setAvePressure(mChatService.proParaBoardData.getAvePressure());
                homeViewModel.getNbpData().setNbpPulse(mChatService.proParaBoardData.getNbpPulseRate());
                homeViewModel.getNbpData().setNbpEndStatus(mChatService.proParaBoardData.isNbpEnd());
                if(mChatService.proParaBoardData.isNbpEnd()){
                    mNbpStartMea = false;
                    mChatService.proParaBoardData.setIsNbpEnd(false);
                    //发送血压结束测量命令
                    mChatService.write(mStopMeaBuffer);
                    ColorStateList colorStateList1 = ContextCompat.getColorStateList(getApplicationContext(),
                            R.color.colorLightGray);
                    messurefab.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
                    messurefab.setBackgroundTintList(colorStateList1);
                    isMessure = false;
                }
            }

        }

    }
}
