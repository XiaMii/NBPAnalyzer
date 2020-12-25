package com.example.nbpanalyzer.Bean;

import com.example.nbpanalyzer.utils.PackUnPack;

/**
 * 蓝牙传输中，存放解包后的血压数据类
 */
public class ProParaBoardData extends PackUnPack {

    /**
     * 血压参数 袖带压、收缩压、舒张压、平均圧和脉率
     */
    private int mNbpCufPressure;
    private int mSysPressure;
    private int mDisPressure;
    private int mAvePressure;
    private int mNbpPulseRate;

    /**
     * 血压测量结束标志位
     */
    private boolean nbpEnd;


    /**
     * @method 类的构造函数，初始化该模块
     */
    public ProParaBoardData() {
        //血压参数初始化
        mNbpCufPressure = 0;
        mSysPressure = 0;
        mDisPressure = 0;
        mAvePressure = 0;
        mNbpPulseRate = 0;
        nbpEnd = false;
    }
    /**
     * @method 获取袖带压值
     * @return 袖带压
     */
    public int getNbpCufPre() {
        return (mNbpCufPressure);
    }

    /**
     * @method 获取收缩压值
     * @return 收缩压
     */
    public int getSysPressure() {
        return (mSysPressure);
    }

    /**
     * @method 获取舒张压值
     * @return 舒张压
     */
    public int getDisPressure() {
        return (mDisPressure);
    }

    /**
     * @method 获取平均压值
     * @return 平均圧
     */
    public int getAvePressure() {
        return (mAvePressure);
    }

    /**
     * @method 获取脉率
     * @return 脉率
     */
    public int getNbpPulseRate() {
        return (mNbpPulseRate);
    }

    /**
     * @method 获取血压测量结束标志位
     * @return 血压测量结束标志位
     */
    public boolean isNbpEnd() {
        return nbpEnd;
    }

    /**
     * @method 设置血压测量标志位
     * @param isNbpEnd 结束测量标志位
     */
    public void setIsNbpEnd(boolean isNbpEnd) {
        this.nbpEnd = isNbpEnd;
    }

    /**
     * @method 处理袖带圧数据包
     * @param unpacked 已解包的袖带压数据包
     */
    private void proNbpCufPre(int[] unpacked) {
        mNbpCufPressure = (unpacked[2] << 8) | unpacked[3];
    }

    /**
     * @method 处理血压测量结束标志位
     * @param unpacked 血压测量结束包
     */
    private void proNbpEnd(int[] unpacked) {
        int data;
        data = unpacked[3];

        if (data != 0) {
            nbpEnd = true;
        }
    }

    /**
     * @method 处理无创血压测量结果1包
     * @param unpacked 无创血压测量结果1包
     */
    private void proNbpResult1(int[] unpacked) {
        mSysPressure = (unpacked[2] << 8) | unpacked[3];
        mDisPressure = (unpacked[4] << 8) | unpacked[5];
        mAvePressure = (unpacked[6] << 8) | unpacked[7];
    }

    /**
     * @method 处理无创血压测量结果2包
     * @param unpacked 无创血压测量结果2包
     */
    private void proNbpResult2(int[] unpacked) {
        mNbpPulseRate = (unpacked[2] << 8) | unpacked[3];
        nbpEnd = true;
    }

    /**
     * @method 根据血压二级id处理血压数据
     * @param unpacked 已解包的血压数据包
     */
    private void proNbpData(int[] unpacked) {
        switch (unpacked[1]) {
            case DAT_NIBP_CUFPRE:
                proNbpCufPre(unpacked);
                break;
            case DAT_NIBP_END:
                proNbpEnd(unpacked);
                break;
            case DAT_NIBP_RSLT1:
                proNbpResult1(unpacked);
                break;
            case DAT_NIBP_RSLT2:
                proNbpResult2(unpacked);
                break;
            default:
                break;
        }
    }
    /**
     * @method 根据模块id分别处理数据包
     * @param unpacked 已解包的数据包
     */
    public void proData(int[] unpacked) {
        int recPacketId = unpacked[0];
        switch (recPacketId) {
            case MODULE_NBP:
                proNbpData(unpacked);
                break;
            default:
                break;
        }
    }

}

