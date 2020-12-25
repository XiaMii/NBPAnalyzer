package com.example.nbpanalyzer.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.nbpanalyzer.utils.DbManger;
import com.example.nbpanalyzer.Bean.NbpData;
import com.example.nbpanalyzer.R;
import com.example.nbpanalyzer.model.HomeViewModel;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private static final String TAG = "HomeFragment";
    private TextView nbpPulseText;
    private TextView cuffPressureText;
    private TextView sysPressureText;
    private TextView disPressureText;
    private TextView tv_date;
    private Button recordButton;
    private DbManger dbManger;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbManger = DbManger.getInstance(view.getContext());

        tv_date = view.findViewById(R.id.tv_Date);
        nbpPulseText = view.findViewById(R.id.tv_pr_data);
        cuffPressureText  = view.findViewById(R.id.tv_Cpressure);
        sysPressureText= view.findViewById(R.id.tv_sys_pressure);
        disPressureText = view.findViewById(R.id.tv_dis_pressure);
        recordButton = view.findViewById(R.id.bt_record);
        //TextView avePressureText;
        //刷新时间广播
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        view.getContext().registerReceiver(receiver,filter);

        homeViewModel.getDate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_date.setText(s);
            }
        });
        homeViewModel.getNbpData().observe(this, new Observer<NbpData>()
        { //注册观察者,观察数据的变化
            @Override
            public void onChanged(NbpData Data) {
                if(nbpPulseText.isEnabled()){
                    nbpPulseText.setKeyListener(null);//去掉点击时编辑框下面横线:
                    sysPressureText.setKeyListener(null);//去掉点击时编辑框下面横线
                    disPressureText.setKeyListener(null);//去掉点击时编辑框下面横线:
                }

                cuffPressureText.setText(String.valueOf(homeViewModel.getNbpData().getNbpCuff()));
                if(homeViewModel.getNbpData().getNbpEndStatus()){
                    recordButton.setClickable(false);
                    String nbpPulse = String.valueOf(homeViewModel.getNbpData().getNbpPulse());
                    String sysPressure = String.valueOf(homeViewModel.getNbpData().getSysPressure());
                    String disPressure = String.valueOf(homeViewModel.getNbpData().getDisPressure());

                    sysPressureText.setText(sysPressure);
                    disPressureText.setText(disPressure);
                    nbpPulseText.setText(nbpPulse);

                        String[] datetime = tv_date.getText().toString().split(" ",2);
                        String date = datetime[0];
                        String time = datetime[1].replace(" ","");
                        //更新历史记录
                        dbManger.addData("default", date, time,
                                Integer.parseInt(disPressure),
                                Integer.parseInt(sysPressure),
                                Integer.parseInt(nbpPulse));
                    //mChatService.proParaBoardData.setIsNbpEnd(false);
                    //mNbpStartMea = false;
                    homeViewModel.getNbpData().setNbpEndStatus(false);
                    recordButton.setClickable(true);
                }
                //nbpPulseText.setText("");
                //sysPressureText.setText("");
                //disPressureText.setText("");//清零

            }
        });


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: 已经点击");
                String nbpPulse= nbpPulseText.getText().toString();
                String sysPressure = sysPressureText.getText().toString();
                String disPressure = disPressureText.getText().toString();
                if(nbpPulse.isEmpty()||sysPressure.isEmpty()||disPressure.isEmpty()){
                }
                else {
                    String[] datetime = tv_date.getText().toString().split(" ",2);
                    String date = datetime[0];
                    String time = datetime[1].replace(" ","");
                    //更新历史记录
                    dbManger.addData("default", date, time,
                            Integer.parseInt(disPressure),
                            Integer.parseInt(sysPressure),
                            Integer.parseInt(nbpPulse));
                    nbpPulseText.setText("");
                    sysPressureText.setText("");
                    disPressureText.setText("");//清零
                }


//                try {
//                    FileOutputStream outStream= getActivity().openFileOutput("Nbpdata.txt",
//                            Context.MODE_APPEND);
//                    outStream.write(nbpPulse);
//                    outStream.close();
//                } catch (FileNotFoundException e) {
//                    return;
//                }
//                catch (IOException e){
//                    return ;
//                }
            }
        });
        return view;
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {//刷新时间
                String s = homeViewModel.getDate().getValue();
                tv_date.setText(s);
            }
        }
    };
}