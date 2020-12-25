package com.example.nbpanalyzer.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecordViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RecordViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("日期    时间      高压      低压      脉率");
    }

    public LiveData<String> getText() {
        return mText;
    }
}