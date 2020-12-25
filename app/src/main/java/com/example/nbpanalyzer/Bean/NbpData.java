package com.example.nbpanalyzer.Bean;

import androidx.lifecycle.LiveData;

/**
 * 实体类：血压数据
 */
public class NbpData extends LiveData<NbpData> {
    /**
     * 血压数据
     */
    private int NbpCuff;
    private int NbpPulse;
    private int SysPressure;
    private int DisPressure;
    private int AvePressure;

    private boolean NbpRun ;
    private boolean NbpStartMea ;
    private boolean NbpEndStatus ;

    public NbpData() {
        NbpCuff = 0;
        NbpPulse = 0;
        SysPressure = 0;
        DisPressure = 0;
        AvePressure = 0;

        NbpRun = false;
        NbpStartMea = false;
        NbpEndStatus = false;
    }
    public int getNbpCuff() {
        return NbpCuff;

    }
    public void setNbpCuff(int NbpCuff) {
        this.NbpCuff = NbpCuff;
        postValue(this);
    }

    public int getNbpPulse() {
        return NbpPulse;
    }

    public void setNbpPulse(int NbpPulse) {
        this.NbpPulse = NbpPulse;
        postValue(this);
    }

    public int getSysPressure() {
        return SysPressure;

    }
    public void setSysPressure(int SysPressure) {
        this.SysPressure = SysPressure;
        postValue(this);
    }
    public boolean getNbpStartMea() {
        return NbpStartMea ;
    }

    public void setNbpStartMea(boolean NbpStartMea) {
        this.NbpStartMea = NbpStartMea;
        postValue(this);
    }
    public boolean getNbpEndStatus() {
        return NbpEndStatus ;
    }

    public void setNbpEndStatus(boolean NbpEndStatus) {
        this.NbpEndStatus = NbpEndStatus;
        postValue(this);
    }
    public boolean getNbpRun() {
        return NbpRun ;
    }

    public void setNbpRun(boolean NbpRun) {
        this.NbpRun = NbpRun;
        postValue(this);
    }
    public int getAvePressure() {
        return AvePressure;

    }
    public void setAvePressure(int AvePressure) {
        this.AvePressure = AvePressure;
        postValue(this);
    }

    public int getDisPressure() {
        return DisPressure;
    }

    public void setDisPressure(int DisPressure) {
        this.DisPressure = DisPressure;
        postValue(this);
    }
}
