package com.example.nbpanalyzer.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nbpanalyzer.Bean.NbpData;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> currentTimeText;
    private NbpData mNbpData;

    private SimpleDateFormat simpleDateFormat;
    private Date date;

    public HomeViewModel() {
        currentTimeText = new MutableLiveData<>();
        //NbpData = new LiveData<>();

        mNbpData = new NbpData();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        date = new Date(System.currentTimeMillis());//获取系统时间
        currentTimeText.setValue(simpleDateFormat.format(date));

    }

    public LiveData<String> getDate() {
        date = new Date(System.currentTimeMillis());//获取系统时间
        currentTimeText.setValue(simpleDateFormat.format(date));
        return currentTimeText;
    }
    public NbpData getNbpData() {
        return mNbpData;
    }
}