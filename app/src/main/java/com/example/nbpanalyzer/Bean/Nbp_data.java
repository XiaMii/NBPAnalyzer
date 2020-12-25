package com.example.nbpanalyzer.Bean;

import android.view.View;

import com.example.nbpanalyzer.R;

/**
 * 数据库操作中用来保存数据的类
 */
public class Nbp_data {

    private String USER;
    private String DATE;
    private String TIME;
    private int SYS;
    private int DIS;
    private int PR;
    private int ID;

    public Nbp_data( int ID, String USER, String DATE, String TIME, int SYS, int DIS, int PR) {
        this.USER = USER;
        this.DATE = DATE;
        this.TIME = TIME;
        this.SYS = SYS;
        this.DIS = DIS;
        this.PR = PR;
        this.ID = ID;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public void setSYS(int SYS) {
        this.SYS = SYS;
    }
    public void setDIS(int DIS) {
        this.DIS = DIS;
    }
    public void setPR(int PR) {
        this.PR = PR;
    }




    public String getUSER() {
        return USER;
    }
    public int getID(){return ID;}
    public int getTimeImage(){
        String[] time = TIME.split(":");
        int imageId;
        //time[0] = time[0].replace(" ","");
        if(Integer.valueOf(time[0])<11)
        {
            imageId = R.drawable.ic_morning;
        }
        else if(Integer.valueOf(time[0])<16)
        {
            imageId = R.drawable.ic_noom;
        }
        else {
            imageId = R.drawable.ic_night;
        }
        return imageId;
    }
    public String getDATE() {
        return DATE;
    }
    public String getTIME() {
        return TIME;
    }
    public int getSYS() { return SYS; }
    public int getDIS() {
        return DIS;
    }
    public int getPR() {
        return PR;
    }
}
