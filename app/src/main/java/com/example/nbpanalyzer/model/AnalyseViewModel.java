package com.example.nbpanalyzer.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnalyseViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AnalyseViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("可视化功能：敬请期待");
    }

    public LiveData<String> getText() {
        return mText;
    }
}